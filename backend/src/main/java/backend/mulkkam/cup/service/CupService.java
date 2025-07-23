package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CupService {

    private final CupRepository cupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CupResponse create(
            CupRegisterRequest cupRegisterRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        List<Cup> cups = cupRepository.findAllByMemberIdOrderByCupRankAsc(memberId);

        CupRank currentCupRank = new CupRank(cups.size());

        Cup cup = new Cup(
                member,
                new CupNickname(cupRegisterRequest.cupNickname()),
                new CupAmount(cupRegisterRequest.cupAmount()),
                currentCupRank.nextRank()
        );

        Cup createdCup = cupRepository.save(cup);
        return new CupResponse(createdCup);
    }

    public CupsResponse readCupsByMemberId(Long memberId) {
        List<Cup> cups = cupRepository.findAllByMemberIdOrderByCupRankAsc(memberId);
        return new CupsResponse(cups);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
