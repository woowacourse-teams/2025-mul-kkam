package backend.mulkkam.admin.dto.response;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record GetAdminIntakeHistoryDetailResponse(
        Long id,
        Long memberId,
        String memberNickname,
        LocalDate historyDate,
        Integer targetAmount,
        Integer streak,
        List<GetAdminIntakeHistoryDetailItemResponse> details,
        LocalDateTime createdAt
) {
    public static GetAdminIntakeHistoryDetailResponse from(IntakeHistory history) {
        List<GetAdminIntakeHistoryDetailItemResponse> details = history.getIntakeHistoryDetails() != null
                ? history.getIntakeHistoryDetails().stream()
                        .map(GetAdminIntakeHistoryDetailItemResponse::from)
                        .toList()
                : List.of();

        return new GetAdminIntakeHistoryDetailResponse(
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
