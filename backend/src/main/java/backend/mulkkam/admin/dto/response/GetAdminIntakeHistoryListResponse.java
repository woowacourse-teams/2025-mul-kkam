package backend.mulkkam.admin.dto.response;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetAdminIntakeHistoryListResponse(
        Long id,
        Long memberId,
        String memberNickname,
        LocalDate historyDate,
        Integer targetAmount,
        Integer streak,
        int detailCount,
        LocalDateTime createdAt
) {
    public static GetAdminIntakeHistoryListResponse from(IntakeHistory history) {
        return new GetAdminIntakeHistoryListResponse(
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
