package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;
import java.time.LocalDateTime;

public record IntakeHistoryResponse(
        Long id,
        LocalDateTime dateTime,
        Amount intakeAmount
) {
}
