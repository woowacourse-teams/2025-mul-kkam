package backend.mulkkam.intake.dto.request;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "직접 입력으로 음용량 기록 생성 요청")
public record CreateIntakeHistoryDetailByUserInputRequest(
        @Schema(description = "섭취 일시", example = "2024-01-15T14:30:00")
        LocalDateTime dateTime,

        @Schema(description = "음용 타입", example = "WATER")
        IntakeType intakeType,

        @Schema(description = "섭취량", example = "200", minimum = "1", maximum = "2000") // TODO 2025. 8. 20. 17:00: 정책 확인할 것
        int intakeAmount
) {
    public IntakeHistoryDetail toIntakeDetail(
            IntakeHistory intakeHistory,
            CupEmojiUrl cupEmojiUrl
    ) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeHistoryDetail(time, intakeHistory, intakeType, intakeAmount, cupEmojiUrl);
    }
}
