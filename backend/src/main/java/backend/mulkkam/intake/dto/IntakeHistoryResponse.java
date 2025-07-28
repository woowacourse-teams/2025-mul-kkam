package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDateTime;

public record IntakeHistoryResponse(
        Long id,
        LocalDateTime dateTime,
        int intakeAmount
) {
    public IntakeHistoryResponse(IntakeHistory intakeHistory) {
        this(
                intakeHistory.getId(),
                intakeHistory.getDateTime(),
                intakeHistory.getIntakeAmount().value()
        );
    }
}
