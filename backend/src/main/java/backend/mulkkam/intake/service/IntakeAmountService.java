package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.RecommendAmount;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;
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
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Transactional
    public void modifyTarget(
            Member member,
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        TargetAmount updateAmount = intakeTargetAmountModifyRequest.toAmount();
        member.updateTargetAmount(updateAmount);
        memberRepository.save(member);

        updateTargetAmountSnapshot(member);
        intakeHistoryRepository.findByMemberAndHistoryDate(member, LocalDate.now())
                .ifPresent(intakeHistory -> intakeHistory.modifyTargetAmount(updateAmount));
    }

    @Transactional
    public void modifyDailyTargetBySuggested(
            Member member,
            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest
    ) {
        Optional<IntakeHistory> intakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member,
                LocalDate.now());

        if (intakeHistory.isPresent()) {
            intakeHistory.get().modifyTargetAmount(modifyIntakeTargetAmountByRecommendRequest.toAmount());
            return;
        }
        int streak = findStreak(member, LocalDate.now());
        IntakeHistory newIntakeHistory = new IntakeHistory(member, LocalDate.now(),
                modifyIntakeTargetAmountByRecommendRequest.toAmount(), streak);

        intakeHistoryRepository.save(newIntakeHistory);
        newIntakeHistory.modifyTargetAmount(modifyIntakeTargetAmountByRecommendRequest.toAmount());
    }

    public IntakeRecommendedAmountResponse getRecommended(Member member) {
        PhysicalAttributes physicalAttributes = member.getPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new IntakeRecommendedAmountResponse(recommendedTargetAmount.value());
    }

    public IntakeTargetAmountResponse getTarget(Member member) {
        return new IntakeTargetAmountResponse(member.getTargetAmount());
    }

    public RecommendedIntakeAmountResponse getRecommendedTargetAmount(
            PhysicalAttributesRequest physicalAttributesRequest
    ) {
        PhysicalAttributes physicalAttributes = physicalAttributesRequest.toPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new RecommendedIntakeAmountResponse(recommendedTargetAmount.value());
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

    private int findStreak(Member member, LocalDate todayDate) {
        Optional<IntakeHistory> yesterdayIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(
                member, todayDate.minusDays(1));
        return yesterdayIntakeHistory.map(intakeHistory -> intakeHistory.getStreak() + 1).orElse(1);
    }
}
