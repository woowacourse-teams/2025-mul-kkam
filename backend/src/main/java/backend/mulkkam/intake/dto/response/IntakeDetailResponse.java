package backend.mulkkam.intake.dto.response;

import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import java.time.LocalTime;

public record IntakeDetailResponse(
        Long id,
        LocalTime time,
        int intakeAmount
) {
    public IntakeDetailResponse(IntakeHistoryDetail intakeDetail) {
        this(
                intakeDetail.getId(),
                intakeDetail.getIntakeTime(),
                intakeDetail.getIntakeAmount().value()
        );
    }
}
