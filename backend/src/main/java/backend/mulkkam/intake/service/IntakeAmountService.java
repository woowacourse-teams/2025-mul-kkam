package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeTargetAmountResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeAmountService {

    private final MemberRepository memberRepository;

    @Transactional
    public void modifyTarget(
            Member member,
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        member.updateTargetAmount(intakeTargetAmountModifyRequest.toAmount());
        memberRepository.save(member);
    }

    public IntakeRecommendedAmountResponse getRecommended(Member member) {
        double weight = member.getPhysicalAttributes().getWeight();
        return new IntakeRecommendedAmountResponse(new Amount((int) (weight * 30)));
    }

    public IntakeTargetAmountResponse getTarget(Member member) {
        return new IntakeTargetAmountResponse(member.getTargetAmount().value());
    }
}
