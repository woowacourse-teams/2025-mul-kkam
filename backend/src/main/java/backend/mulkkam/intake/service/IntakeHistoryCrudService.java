package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IntakeHistoryCrudService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    public boolean isExistIntakeHistory(Member member, LocalDate date) {
        return intakeHistoryRepository.existsByMemberAndHistoryDate(member, date);
    }

    public IntakeHistoryDetail createIntakeHistoryDetail(IntakeHistoryDetail intakeHistoryDetail) {
        return intakeHistoryDetailRepository.save(intakeHistoryDetail);
    }

    public List<IntakeHistory> getIntakeHistories(Member member) {
        return intakeHistoryRepository.findAllByMember(member);
    }

    public IntakeHistory getIntakeHistory(Member member, LocalDate intakeDate) {
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, intakeDate)
                .orElseGet(() -> getInitializedHistory(member, intakeDate));
    }

    private IntakeHistory getInitializedHistory(Member member, LocalDate intakeDate) {
        int streak = getStreak(member, intakeDate);
        final IntakeHistory intakeHistory = new IntakeHistory(
                member,
                intakeDate,
                member.getTargetAmount(),
                streak
        );
        return intakeHistoryRepository.save(intakeHistory);
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(Member member, LocalDate date) {
        return getIntakeHistoryDetails(member, date, date);
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(
            Member member,
            LocalDate from,
            LocalDate to
    ) {
        return intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, from, to);
    }

    public int getTotalIntakeAmount(Member member, LocalDate date) {
        List<IntakeHistoryDetail> details = getIntakeHistoryDetails(member, date);
        return details.stream()
                .mapToInt(detail -> detail.getIntakeAmount().value())
                .sum();
    }

    public int getStreak(Member member, LocalDate date) {
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

    public void deleteIntakeHistoryDetail(Member member, Long id) {
        intakeHistoryDetailRepository.findById(id)
                .ifPresent((detail) -> {
                    validateAbleToDelete(member, detail);
                    intakeHistoryDetailRepository.delete(detail);
                });
    }

    private void validateAbleToDelete(Member member, IntakeHistoryDetail detail) {
        if (!detail.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_INTAKE_HISTORY);
        }
        LocalDate today = LocalDate.now();
        if (!detail.isCreatedAt(today)) {
            throw new CommonException(INVALID_DATE_FOR_DELETE_INTAKE_HISTORY);
        }
    }
}
