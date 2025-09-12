package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.NOT_ALL_MEMBER_CUPS_INCLUDED;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.NOT_EXIST_DEFAULT_CUP_EMOJI;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP_EMOJI;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.EmojiCode;
import backend.mulkkam.cup.domain.collection.CupRanks;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CreateCup;
import backend.mulkkam.cup.dto.CreateCupRanked;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.CreateCupWithoutRankRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.dto.response.DefaultCupResponse;
import backend.mulkkam.cup.dto.response.DefaultCupsResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CupService {

    private static final int MAX_CUP_COUNT = 3;

    private final CupRepository cupRepository;
    private final MemberRepository memberRepository;
    private final CupEmojiRepository cupEmojiRepository;

    @Transactional
    public void reset(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        List<Cup> defaultCups = getDefaultCups(member);
        cupRepository.deleteByMember(member);
        cupRepository.saveAll(defaultCups);
    }

    private List<Cup> getDefaultCups(Member member) {
        Map<EmojiCode, CupEmoji> emojiByCode = getDefaultEmojiByCode();
        return Arrays.stream(DefaultCup.values())
                .map(defaultCup -> {
                    EmojiCode code = defaultCup.getCode();
                    return new Cup(
                            member,
                            defaultCup.getNickname(),
                            defaultCup.getAmount(),
                            defaultCup.getRank(),
                            defaultCup.getIntakeType(),
                            emojiByCode.get(code)
                    );
                })
                .toList();
    }

    public DefaultCupsResponse readDefaultCups() {
        Map<EmojiCode, CupEmoji> emojiByCode = getDefaultEmojiByCode();

        List<DefaultCupResponse> defaultCups = Arrays.stream(DefaultCup.values())
                .map(defaultCup -> new DefaultCupResponse(defaultCup, emojiByCode.get(defaultCup.getCode())))
                .toList();
        return new DefaultCupsResponse(defaultCups);
    }

    public Map<EmojiCode, CupEmoji> getDefaultEmojiByCode() {
        List<CupEmoji> defaultEmojis = getSavedDefaultCupEmojis();

        Map<EmojiCode, CupEmoji> result = defaultEmojis.stream()
                .collect(Collectors.toMap(CupEmoji::getCode, Function.identity()));
        Set<EmojiCode> expectedDefaultEmojis = Arrays.stream(DefaultCup.values())
                .map(DefaultCup::getCode)
                .collect(Collectors.toSet());

        if (result.keySet().containsAll(expectedDefaultEmojis)) {
            return result;
        }
        throw new CommonException(NOT_EXIST_DEFAULT_CUP_EMOJI);
    }

    private List<CupEmoji> getSavedDefaultCupEmojis() {
        List<EmojiCode> defaultEmojiCodes = Arrays.stream(DefaultCup.values())
                .map(DefaultCup::getCode)
                .distinct()
                .toList();
        return cupEmojiRepository.findAllByCodeIn(defaultEmojiCodes);
    }

    public CupsResponse readSortedCupsByMember(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        List<Cup> cups = cupRepository.findAllByMemberOrderByCupRankAsc(member);
        return new CupsResponse(cups.stream()
                .map(CupResponse::new)
                .toList()
        );
    }

    @Transactional
    public void createAll(
            List<CreateCupRequest> cupRequests,
            Member member
    ) {
        List<CreateCup> createCups = toCreateCups(cupRequests);
        List<CupRank> cupRanks = createCups.stream()
                .map(CreateCup::cupRank)
                .toList();
        validateDistinctRanks(cupRanks);

        List<Cup> cups = createCups.stream()
                .map(o -> o.toCup(member))
                .toList();
        cupRepository.saveAll(cups);
    }

    @Transactional
    public CupResponse createAtLastRank(
            CreateCupWithoutRankRequest createCupWithoutRankRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        CupEmoji cupEmoji = getCupEmoji(createCupWithoutRankRequest.cupEmojiId());
        CreateCupRanked createCupRanked = createCupWithoutRankRequest.toCreateCupRanked(calculateNextCupRank(member), cupEmoji);

        Cup cup = createCupRanked.toCup(member);
        cupRepository.save(cup);

        return new CupResponse(cup);
    }

    @Transactional
    public CupsRanksResponse updateRanks(
            UpdateCupRanksRequest request,
            MemberDetails memberDetails
    ) {
        CupRanks cupRanks = new CupRanks(buildCupRankMapById(request.cups()));
        Member member = getMember(memberDetails.id());
        List<Cup> cups = getAllByIdsAndMemberId(cupRanks.getCupIds(), member);

        for (Cup cup : cups) {
            cup.modifyRank(cupRanks.getCupRank(cup.getId()));
        }

        return new CupsRanksResponse(
                cups.stream()
                        .map(CupRankDto::new)
                        .toList()
        );
    }

    @Transactional
    public void update(
            Long cupId,
            MemberDetails memberDetails,
            UpdateCupRequest updateCupRequest
    ) {
        Cup cup = getCup(cupId);
        Member member = getMember(memberDetails.id());
        CupEmoji cupEmoji = getCupEmoji(updateCupRequest.cupEmojiId());

        validateCupOwnership(member, cup);
        cup.update(
                new CupNickname(updateCupRequest.cupNickname()),
                new CupAmount(updateCupRequest.cupAmount()),
                updateCupRequest.intakeType(),
                cupEmoji
        );
    }

    @Transactional
    public void delete(
            Long cupId,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        Cup targetCup = cupRepository.findByIdAndMember(cupId, member)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));

        cupRepository.delete(targetCup);

        cupRepository.findAllByMember(member)
                .stream()
                .filter(cup -> cup.isLowerPriorityThan(targetCup))
                .forEach(Cup::promoteRank);
    }



    private CupRank calculateNextCupRank(Member member) {
        final int cupCount = cupRepository.countByMemberId(member.getId());
        if (cupCount >= MAX_CUP_COUNT) {
            throw new CommonException(INVALID_CUP_COUNT);
        }
        return new CupRank(cupCount + 1);
    }

    private List<CreateCup> toCreateCups(List<CreateCupRequest> cupRequests) {
        List<CreateCup> createCups = new ArrayList<>();
        for (CreateCupRequest createCupRequest : cupRequests) {
            CupRank cupRank = new CupRank(createCupRequest.cupRank());
            CupEmoji cupEmoji = getCupEmoji(createCupRequest.cupEmojiId());
            createCups.add(createCupRequest.toCreateCup(cupRank, cupEmoji));
        }
        return createCups;
    }

    private Map<Long, CupRank> buildCupRankMapById(List<CupRankDto> cupRanks) {
        Map<Long, CupRank> ranks = new HashMap<>();
        for (CupRankDto cup : cupRanks) {
            if (ranks.containsKey(cup.id())) {
                throw new CommonException(DUPLICATED_CUP);
            }
            ranks.put(cup.id(), new CupRank(cup.rank()));
        }
        return ranks;
    }

    private void validateDistinctRanks(List<CupRank> cupRanks) {
        Set<CupRank> distinctCupRanks = new HashSet<>(cupRanks);

        if (distinctCupRanks.size() != cupRanks.size()) {
            throw new CommonException(DUPLICATED_CUP_RANKS);
        }
    }

    private List<Cup> getAllByIdsAndMemberId(
            Set<Long> cupIds,
            Member member
    ) {
        List<Cup> cups = cupRepository.findAllById(cupIds);
        if (cups.size() != cupIds.size()) {
            throw new CommonException(NOT_FOUND_CUP);
        }
        validateCupsOwnership(cups, member);
        return cups;
    }

    private void validateCupsOwnership(
            List<Cup> cups,
            Member member
    ) {
        for (Cup cup : cups) {
            validateCupOwnership(member, cup);
        }

        List<Cup> cupsByMember = cupRepository.findAllByMember(member);

        validateAllCupsByMember(cups, cupsByMember);
    }

    private void validateAllCupsByMember(
            List<Cup> cups,
            List<Cup> allByMember
    ) {
        Set<Long> memberCupIds = allByMember.stream()
                .map(Cup::getId)
                .collect(Collectors.toSet());

        Set<Long> cupIds = cups.stream()
                .map(Cup::getId)
                .collect(Collectors.toSet());

        if (!memberCupIds.equals(cupIds)) {
            throw new CommonException(NOT_ALL_MEMBER_CUPS_INCLUDED);
        }
    }

    private void validateCupOwnership(Member member, Cup cup) {
        if (!cup.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_CUP);
        }
    }

    private CupEmoji getCupEmoji(Long emojiId) {
        return cupEmojiRepository.findById(emojiId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP_EMOJI));
    }

    private Cup getCup(final Long id) {
        return cupRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
    }

    private Member getMember(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
