package backend.mulkkam.intake.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record IntakeHistorySummaryResponse(
        LocalDate date,
        int targetAmount,
        int totalIntakeAmount,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.0")
        double achievementRate,
        int streak,
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
