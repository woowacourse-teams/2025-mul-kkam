package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeAmountService {

    private final MemberRepository memberRepository;
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Transactional
    public void modifyTarget(
            MemberDetails memberDetails,
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        Member member = getMember(memberDetails.id());
        TargetAmount updateAmount = intakeTargetAmountModifyRequest.toAmount();
        member.updateTargetAmount(updateAmount);
        memberRepository.save(member);

        updateTargetAmountSnapshot(member);
        intakeHistoryRepository.findByMemberAndHistoryDate(member, LocalDate.now())
                .ifPresent(intakeHistory -> intakeHistory.modifyTargetAmount(updateAmount));
    }

    @Transactional
    public void modifyDailyTargetBySuggested(
            MemberDetails memberDetails,
            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest
    ) {
        LocalDate now = LocalDate.now();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member, now)
                .orElseGet(() -> {
                    int streak = findStreak(member, now);
                    IntakeHistory newIntakeHistory = new IntakeHistory(
                            member,
                            now,
                            modifyIntakeTargetAmountByRecommendRequest.toAmount(),
                            streak
                    );
                    return intakeHistoryRepository.save(newIntakeHistory);
                });

        intakeHistory.modifyTargetAmount(modifyIntakeTargetAmountByRecommendRequest.toAmount());
    }

    public IntakeRecommendedAmountResponse getRecommended(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        PhysicalAttributes physicalAttributes = member.getPhysicalAttributes();
        RecommendAmount recommendedTargetAmount = new RecommendAmount(physicalAttributes);
        return new IntakeRecommendedAmountResponse(recommendedTargetAmount.value());
    }

    public IntakeTargetAmountResponse getTarget(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
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
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, todayDate.minusDays(1))
                .map(intakeHistory -> intakeHistory.getStreak() + 1)
                .orElse(1);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
