package backend.mulkkam.admin.dto.response;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AdminIntakeHistoryDetailResponse(
        Long id,
        Long memberId,
        String memberNickname,
        LocalDate historyDate,
        Integer targetAmount,
        Integer streak,
        List<AdminIntakeHistoryDetailItemResponse> details,
        LocalDateTime createdAt
) {
    public static AdminIntakeHistoryDetailResponse from(IntakeHistory history) {
        List<AdminIntakeHistoryDetailItemResponse> details = history.getIntakeHistoryDetails() != null
                ? history.getIntakeHistoryDetails().stream()
                        .map(AdminIntakeHistoryDetailItemResponse::from)
                        .toList()
                : List.of();

        return new AdminIntakeHistoryDetailResponse(
                history.getId(),
                history.getMember().getId(),
                history.getMember().getMemberNickname() != null ? history.getMember().getMemberNickname().value() : null,
                history.getHistoryDate(),
                history.getTargetAmount() != null ? history.getTargetAmount().value() : null,
                history.getStreak(),
                details,
                history.getCreatedAt()
        );
    }
}
