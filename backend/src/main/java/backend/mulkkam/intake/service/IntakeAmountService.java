package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.RecommendAmount;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
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
        PhysicalAttributes physicalAttributes = member.getPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new IntakeRecommendedAmountResponse(recommendedTargetAmount.amount());
    }

    public IntakeTargetAmountResponse getTarget(Member member) {
        return new IntakeTargetAmountResponse(member.getTargetAmount());
    }

    public RecommendedIntakeAmountResponse getRecommendedTargetAmount(
            PhysicalAttributesRequest physicalAttributesRequest
    ) {
        PhysicalAttributes physicalAttributes = physicalAttributesRequest.toPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new RecommendedIntakeAmountResponse(recommendedTargetAmount.amount());
    }
}
