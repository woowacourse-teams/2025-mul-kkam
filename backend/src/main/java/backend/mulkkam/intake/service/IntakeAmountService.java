package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.NotFoundErrorCode;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeAmountResponse;
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
            IntakeAmountModifyRequest intakeAmountModifyRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);

        member.updateTargetAmount(intakeAmountModifyRequest.toAmount());
    }

    public IntakeAmountResponse getRecommended(Long memberId) {
        Member member = getMember(memberId);

        IntakeCondition intakeCondition = new IntakeCondition(member.getPhysicalAttributes());
        WaterIntakeAmountCalculator waterIntakeAmountCalculator = new WaterIntakeAmountCalculator(intakeCondition);
        Amount recommendedIntakeAmount = waterIntakeAmountCalculator.calculate();

        return new IntakeAmountResponse(recommendedIntakeAmount);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
