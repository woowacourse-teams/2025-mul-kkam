package backend.mulkkam.intake.dto.response;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "음용량 기록 상세 정보")
public record IntakeHistoryDetailResponse(
        @Schema(description = "음용량 기록 상세정보 id", example = "1")
        Long id,

        @Schema(description = "섭취 시간", example = "14:30:00")
        LocalTime time,

        @Schema(description = "섭취량 (ml)", example = "250")
        int intakeAmount,

        @Schema(description = "음용 타입", example = "WATER")
        IntakeType intakeType,

        @Schema(description = "컵 이미지 url", example = "http://example.com")
        String url
) {
    public IntakeHistoryDetailResponse(IntakeHistoryDetail intakeDetail) {
        this(
                intakeDetail.getId(),
                intakeDetail.getIntakeTime(),
                intakeDetail.getIntakeAmount().value(),
                intakeDetail.getIntakeType(),
                intakeDetail.getCup().getCupEmoji().getUrl()
        );
    }
}
