package backend.mulkkam.intake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public record IntakeHistorySummaryResponse(
        LocalDate date,
        int targetAmount,
        int totalIntakeAmount,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.0")
        double achievementRate,
        List<IntakeHistoryResponse> intakeHistories
) {
}
