package backend.mulkkam.cup.service;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CupService {

    private final CupRepository cupRepository;
    private final MemberService memberService;


    public CupResponse create(CupRegisterRequest cupRegisterRequest) {
        Member member = new Member(1L, new MemberNickname("Member"), Gender.MALE, 50, new Amount(1200));
        Cup cup = new Cup(null, member, new CupNickname(cupRegisterRequest.nickname()), cupRegisterRequest.amount());

        Cup createdCup = cupRepository.save(cup);
        return new CupResponse(createdCup.getId(), createdCup.getNickname().value(), createdCup.getAmount());
    }
}
