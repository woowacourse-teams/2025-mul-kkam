package backend.mulkkam.intake.dto.request;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "컵으로 음용량 기록 생성 요청")
public record CreateIntakeHistoryDetailByCupRequest(
        @Schema(description = "섭취 일시", example = "2024-01-15T14:30:00")
        LocalDateTime dateTime,

        @Schema(description = "음용 타입", example = "WATER")
        IntakeType intakeType,

        @Schema(description = "사용한 컵 이모지 Id", example = "1")
        Long cupId
) {
    public IntakeHistoryDetail toIntakeDetail(
            IntakeHistory intakeHistory,
            Cup cup
    ) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeHistoryDetail(time, intakeHistory, intakeType, cup);
    }
}
