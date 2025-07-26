package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeAmountResponse;
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
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        member.updateTargetAmount(intakeTargetAmountModifyRequest.toAmount());
    }

    public IntakeAmountResponse getRecommended(Long memberId) {
        Member member = getMember(memberId);

        double weight = member.getPhysicalAttributes().getWeight();

        return new IntakeAmountResponse(new Amount((int) (weight * 30)));
    }

    public IntakeTargetAmountResponse getTarget(Long memberId) {
        Member member = getMember(memberId);
        return new IntakeTargetAmountResponse(member.getTargetAmount().value());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
