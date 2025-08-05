package backend.mulkkam.intake.dto.response;

import backend.mulkkam.intake.domain.IntakeDetail;
import java.time.LocalTime;

public record IntakeDetailResponse(
        Long id,
        LocalTime time,
        int intakeAmount
) {
    public IntakeDetailResponse(IntakeDetail intakeDetail) {
        this(
                intakeDetail.getId(),
                intakeDetail.getIntakeTime(),
                intakeDetail.getIntakeAmount().value()
        );
    }
}
