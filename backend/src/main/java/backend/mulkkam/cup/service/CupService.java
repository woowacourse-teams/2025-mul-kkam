package backend.mulkkam.cup.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.collection.CupRanks;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CupNicknameAndAmountModifyRequest;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.FORBIDDEN;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CupService {

    private static final int MAX_CUP_COUNT = 3;

    private final CupRepository cupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CupResponse create(
            CupRegisterRequest cupRegisterRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        final int cupCount = cupRepository.countByMemberId(memberId);

        if (cupCount >= MAX_CUP_COUNT) {
            throw new CommonException(INVALID_CUP_COUNT);
        }

        CupRank nextCupRank = new CupRank(cupCount + 1);
        Cup cup = cupRegisterRequest.toCup(
                member,
                nextCupRank
        );
        Cup createdCup = cupRepository.save(cup);

        return new CupResponse(createdCup);
    }

    @Transactional
    public CupsRanksResponse updateRanks(
            UpdateCupRanksRequest request,
            Long memberId
    ) {
        CupRanks cupRanks = new CupRanks(request.cups());
        List<Cup> cups = getAllByIdsAndMemberId(cupRanks.getCupIds(), memberId);

        for (Cup cup : cups) {
            cup.modifyRank(cupRanks.getCupRank(cup.getId()));
        }

        return new CupsRanksResponse(
                cups.stream()
                        .map(CupRankDto::new)
                        .toList()
        );
    }

    private List<Cup> getAllByIdsAndMemberId(Set<Long> cupIds, Long memberId) {
        List<Cup> cups = cupRepository.findAllById(cupIds);
        if (cups.size() != cupIds.size()) {
            throw new CommonException(NOT_FOUND_CUP);
        }
        validateOwnership(cupIds, memberId);
        return cups;
    }

    private void validateOwnership(Set<Long> cupIds, Long memberId) {
        Set<Long> membersCupIds = cupRepository.findAllByMemberId(memberId)
                .stream()
                .map(Cup::getId)
                .collect(Collectors.toSet());

        if (!membersCupIds.containsAll(cupIds)) {
            throw new CommonException(FORBIDDEN);
        }
    }

    @Transactional
    public void delete(
            Long cupId,
            Long memberId
    ) {
        Cup targetCup = cupRepository.findByIdAndMemberId(cupId, memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));

        cupRepository.delete(targetCup);

        cupRepository.findAllByMemberId(memberId)
                .stream()
                .filter(cup -> cup.isLowerPriorityThan(targetCup))
                .forEach(Cup::promoteRank);
    }

    public CupsResponse readCupsByMemberId(Long memberId) {
        List<Cup> cups = cupRepository.findAllByMemberIdOrderByCupRankAsc(memberId);
        return new CupsResponse(cups);
    }

    @Transactional
    public void modifyNicknameAndAmount(
            Long id,
            Long memberId,
            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest
    ) {
        Member member = getMember(memberId);
        Cup cup = getCup(id);

        validateCupOwnership(member, cup);
        cup.modifyNicknameAndAmount(
                new CupNickname(cupNicknameAndAmountModifyRequest.cupNickname()),
                new CupAmount(cupNicknameAndAmountModifyRequest.cupAmount())
        );
    }

    private void validateCupOwnership(Member member, Cup cup) {
        if (member.equals(cup.getMember())) {
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_CUP);
    }

    private Cup getCup(final Long id) {
        return cupRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
