package backend.mulkkam.intake.dto.request;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "음용량 기록 생성 요청")
public record CreateIntakeHistoryDetailRequest(
        @Schema(description = "섭취 일시", example = "2024-01-15T14:30:00")
        LocalDateTime dateTime,

        @Schema(description = "섭취량 (ml)", example = "250", minimum = "1")
        int intakeAmount,

        @Schema(description = "음용 타입", example = "WATER")
        IntakeType intakeType
        int intakeAmount,

        @Schema(description = "사용한 컵 Id", example = "1")
        Long cupId
) {
    public IntakeHistoryDetail toIntakeDetail(
            IntakeHistory intakeHistory,
            IntakeAmount intakeAmount,
            Cup cup
    ) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeHistoryDetail(dateTime.toLocalTime(), intakeAmount.value(), intakeType, intakeHistory, cup);
}
