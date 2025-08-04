package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeTargetAmountResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.NOT_FOUND_MEMBER;

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

        double weight = member.getPhysicalAttributes().getWeight();

        return new IntakeRecommendedAmountResponse(new Amount((int) (weight * 30)));
    }

    public IntakeTargetAmountResponse getTarget(Long memberId) {
        Member member = getMember(memberId);
        return new IntakeTargetAmountResponse(member.getTargetAmount().value());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
