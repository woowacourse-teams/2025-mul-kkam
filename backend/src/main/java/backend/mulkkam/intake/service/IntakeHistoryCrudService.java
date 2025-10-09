package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IntakeHistoryCrudService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    public boolean isExistIntakeHistory(
            Member member,
            LocalDate date
    ) {
        return intakeHistoryRepository.existsByMemberAndHistoryDate(member, date);
    }

    public IntakeHistoryDetail createIntakeHistoryDetail(IntakeHistoryDetail intakeHistoryDetail) {
        return intakeHistoryDetailRepository.save(intakeHistoryDetail);
    }

    public List<IntakeHistory> getIntakeHistories(Member member) {
        return intakeHistoryRepository.findAllByMember(member);
    }

    public List<IntakeHistory> getIntakeHistories(Member member, DateRangeRequest dateRangeRequest) {
        return intakeHistoryRepository.findAllByMemberAndDateRange(member, dateRangeRequest.from(),
                dateRangeRequest.to());
    }

    public IntakeHistory getIntakeHistory(
            Member member,
            LocalDate intakeDate
    ) {
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, intakeDate)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
    }

    public IntakeHistory getOrCreateIntakeHistory(
            Member member,
            LocalDate intakeDate
    ) {
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, intakeDate)
                .orElseGet(() -> getInitializedHistory(member, intakeDate));
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(IntakeHistory intakeHistory) {
        return intakeHistoryDetailRepository.findByIntakeHistory(intakeHistory);
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(
            Member member, DateRangeRequest dateRangeRequest
    ) {
        return intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, dateRangeRequest.from(), dateRangeRequest.to());
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(List<IntakeHistory> intakeHistories) {
        return intakeHistoryDetailRepository.findAllByIntakeHistoryIn(intakeHistories);
    }

    public AchievementRate getAchievementRate(IntakeHistory intakeHistory) {
        TargetAmount targetAmount = intakeHistory.getTargetAmount();
        int totalIntakeAmount = getTotalIntakeAmount(intakeHistory);
        return new AchievementRate(totalIntakeAmount, targetAmount);
    }

    public int getTotalIntakeAmount(IntakeHistory intakeHistory) {
        List<IntakeHistoryDetail> details = getIntakeHistoryDetails(intakeHistory);
        return details.stream()
                .mapToInt(detail -> detail.getIntakeAmount().value())
                .sum();
    }

    public int getStreak(
            Member member,
            LocalDate date
    ) {
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, date.minusDays(1))
                .map(intakeHistory -> intakeHistory.getStreak() + 1)
                .orElse(1);
    }

    public void deleteAllIntakeHistoryDetail(Member member) {
        List<IntakeHistory> histories = getIntakeHistories(member);
        intakeHistoryDetailRepository.deleteAllByIntakeHistoryIn(histories);
    }

    public void deleteAllIntakeHistory(Member member) {
        intakeHistoryRepository.deleteAllByMember(member);
    }

    public void deleteIntakeHistoryDetail(
            Member member,
            Long id
    ) {
        intakeHistoryDetailRepository.findById(id)
                .ifPresent((detail) -> {
                    validateAbleToDelete(member, detail);
                    intakeHistoryDetailRepository.delete(detail);
                });
    }

    private IntakeHistory getInitializedHistory(
            Member member,
            LocalDate intakeDate
    ) {
        int streak = getStreak(member, intakeDate);
        final IntakeHistory intakeHistory = new IntakeHistory(
                member,
                intakeDate,
                member.getTargetAmount(),
                streak
        );
        return intakeHistoryRepository.save(intakeHistory);
    }

    private void validateAbleToDelete(
            Member member,
            IntakeHistoryDetail detail
    ) {
        if (!detail.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_INTAKE_HISTORY);
        }
        LocalDate today = LocalDate.now();
        if (!detail.isCreatedAt(today)) {
            throw new CommonException(INVALID_DATE_FOR_DELETE_INTAKE_HISTORY);
        }
    }
}
