package backend.mulkkam.cup.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.NotFoundErrorCode;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
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
        List<Cup> cups = cupRepository.findAllByMemberId(memberId);

        CupRank currentCupRank = new CupRank(cups.size());

        Cup cup = new Cup(
                member,
                new CupNickname(cupRegisterRequest.nickname()),
                new CupAmount(cupRegisterRequest.amount()),
                currentCupRank.nextRank()
        );

        Cup createdCup = cupRepository.save(cup);
        return new CupResponse(
                createdCup.getId(),
                createdCup.getNickname().value(),
                createdCup.getCupAmount().value()
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
