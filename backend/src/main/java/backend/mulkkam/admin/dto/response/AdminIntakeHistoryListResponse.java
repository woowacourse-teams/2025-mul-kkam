package backend.mulkkam.admin.dto.response;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminIntakeHistoryListResponse(
        Long id,
        Long memberId,
        String memberNickname,
        LocalDate historyDate,
        Integer targetAmount,
        Integer streak,
        int detailCount,
        LocalDateTime createdAt
) {
    public static AdminIntakeHistoryListResponse from(IntakeHistory history) {
        return new AdminIntakeHistoryListResponse(
                history.getId(),
                history.getMember().getId(),
                history.getMember().getMemberNickname() != null ? history.getMember().getMemberNickname().value() : null,
                history.getHistoryDate(),
                history.getTargetAmount() != null ? history.getTargetAmount().value() : null,
                history.getStreak(),
                history.getIntakeHistoryDetails() != null ? history.getIntakeHistoryDetails().size() : 0,
                history.getCreatedAt()
        );
    }
}
