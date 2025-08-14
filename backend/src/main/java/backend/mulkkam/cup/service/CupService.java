package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.NOT_ALL_MEMBER_CUPS_INCLUDED;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.collection.CupRanks;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final CupEmojiRepository cupEmojiRepository;

    public CupsResponse readSortedCupsByMember(Member member) {
        List<Cup> cups = cupRepository.findAllByMemberOrderByCupRankAsc(member);
        return new CupsResponse(cups);
    }

    @Transactional
    public CupResponse create(
            CreateCupRequest registerCupRequest,
            Member member
    ) {
        IntakeType intakeType = IntakeType.findByName(registerCupRequest.intakeType());
        CupEmoji cupEmoji = getCupEmoji(registerCupRequest.emoji());
        Cup cup = registerCupRequest.toCup(member, calculateNextCupRank(member), intakeType, cupEmoji);

        Cup createdCup = cupRepository.save(cup);

        return new CupResponse(createdCup);
    }

    private CupRank calculateNextCupRank(Member member) {
        final int cupCount = cupRepository.countByMemberId(member.getId());
        if (cupCount >= MAX_CUP_COUNT) {
            throw new CommonException(INVALID_CUP_COUNT);
        }
        return new CupRank(cupCount + 1);
    }

    @Transactional
    public CupsRanksResponse updateRanks(
            UpdateCupRanksRequest request,
            Member member
    ) {
        CupRanks cupRanks = new CupRanks(buildCupRankMapById(request.cups()));
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
            Member member,
            UpdateCupRequest updateCupRequest
    ) {
        Cup cup = getCup(cupId);
        CupEmoji cupEmoji = getCupEmoji(updateCupRequest.emoji());

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
            Member member
    ) {
        Cup targetCup = cupRepository.findByIdAndMember(cupId, member)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));

        cupRepository.delete(targetCup);

        cupRepository.findAllByMember(member)
                .stream()
                .filter(cup -> cup.isLowerPriorityThan(targetCup))
                .forEach(Cup::promoteRank);
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

    private CupEmoji getCupEmoji(String emoji) {
        return cupEmojiRepository.findById(Long.parseLong(emoji))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 cup id"));
    }

    private Cup getCup(final Long id) {
        return cupRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
    }
}
