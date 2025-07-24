package backend.mulkkam.intake.dto;

import java.time.LocalDate;
import java.util.List;

public record IntakeHistorySummaryResponse(
        LocalDate date,
        int targetAmount,
        int totalIntakeAmount,
        double achievementRate,
        List<IntakeHistoryResponse> intakeHistories
) {
}
