package backend.mulkkam.intake.dto;

import java.time.LocalDateTime;

public record IntakeHistoryResponse(
        Long id,
        LocalDateTime dateTime,
        int intakeAmount
) {
}
