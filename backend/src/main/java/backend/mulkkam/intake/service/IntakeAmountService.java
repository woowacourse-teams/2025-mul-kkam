package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeTargetAmountResponse;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeAmountService {

    private final MemberRepository memberRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Transactional
    public void modifyTarget(
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        member.updateTargetAmount(intakeTargetAmountModifyRequest.toAmount());
        updateTargetAmountSnapshot(member);
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
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private void updateTargetAmountSnapshot(Member member) {
        LocalDate today = LocalDate.now();
        Optional<TargetAmountSnapshot> targetAmountSnapshot = targetAmountSnapshotRepository.findByMemberIdAndUpdatedAt(
                member.getId(), today);
        if (targetAmountSnapshot.isPresent()) {
            targetAmountSnapshot.get().updateTargetAmount(member.getTargetAmount());
            return;
        }
        targetAmountSnapshotRepository.save(new TargetAmountSnapshot(member, today, member.getTargetAmount()));
    }
}
