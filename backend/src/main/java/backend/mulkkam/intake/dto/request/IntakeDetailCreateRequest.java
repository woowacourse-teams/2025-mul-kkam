package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "음수량 기록 생성 요청")
public record IntakeDetailCreateRequest(
        @Schema(description = "섭취 일시", example = "2024-01-15T14:30:00")
        LocalDateTime dateTime,

        @Schema(description = "섭취량 (ml)", example = "250", minimum = "1")
        int intakeAmount
) {
    public IntakeHistoryDetail toIntakeDetail(IntakeHistory intakeHistory) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeHistoryDetail(time, new IntakeAmount(intakeAmount), intakeHistory);
    }
}
