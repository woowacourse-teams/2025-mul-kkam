package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.IntakeDetail;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record IntakeDetailCreateRequest(
        LocalDateTime dateTime,
        int intakeAmount
) {
    public IntakeDetail toIntakeDetail(IntakeHistory intakeHistory) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeDetail(time, new Amount(intakeAmount), intakeHistory);
    }
}
