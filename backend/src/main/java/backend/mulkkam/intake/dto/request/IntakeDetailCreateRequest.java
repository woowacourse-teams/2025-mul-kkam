package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.Amount;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record IntakeDetailCreateRequest(
        LocalDateTime dateTime,
        int intakeAmount
) {
    public IntakeHistoryDetail toIntakeDetail(IntakeHistory intakeHistory) {
        LocalTime time = dateTime.toLocalTime();
        return new IntakeHistoryDetail(time, new Amount(intakeAmount), intakeHistory);
    }
}
