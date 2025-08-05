package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
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
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        member.updateTargetAmount(intakeTargetAmountModifyRequest.toAmount());
    }

    public IntakeRecommendedAmountResponse getRecommended(Long memberId) {
        Member member = getMember(memberId);

        PhysicalAttributes physicalAttributes = member.getPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new IntakeRecommendedAmountResponse(recommendedTargetAmount.amount());
    }

    public IntakeTargetAmountResponse getTarget(Long memberId) {
        Member member = getMember(memberId);
        return new IntakeTargetAmountResponse(member.getTargetAmount());
    }

    public RecommendedIntakeAmountResponse getRecommendedTargetAmount(
            PhysicalAttributesRequest physicalAttributesRequest) {
        PhysicalAttributes physicalAttributes = physicalAttributesRequest.toPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new RecommendedIntakeAmountResponse(recommendedTargetAmount.amount());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
