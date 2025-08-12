package backend.mulkkam.intake.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Schema(description = "음수량 기록 요약 응답")
public record IntakeHistorySummaryResponse(
        @Schema(description = "기록 날짜", example = "2024-01-15")
        LocalDate date,

        @Schema(description = "목표 음수량 (ml)", example = "2000")
        int targetAmount,

        @Schema(description = "총 섭취량 (ml)", example = "1500")
        int totalIntakeAmount,

        @Schema(description = "달성률 (%)", example = "75.0", minimum = "0.0", maximum = "100.0")
        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.0")
        double achievementRate,

        @Schema(description = "연속 달성 일수", example = "3", minimum = "0")
        int streak,

        @Schema(description = "음수량 상세 기록 목록")
        List<IntakeDetailResponse> intakeDetails
) {
    public IntakeHistorySummaryResponse(LocalDate date, int targetAmount) {
        this(
                date,
                targetAmount,
                0,
                0.0,
                0,
                Collections.emptyList()
        );
    }

    public IntakeHistorySummaryResponse(LocalDate date) {
        this(
                date,
                0,
                0,
                0.0,
                0,
                Collections.emptyList()
        );
    }
}
