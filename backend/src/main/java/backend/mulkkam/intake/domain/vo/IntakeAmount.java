package backend.mulkkam.intake.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_INTAKE_AMOUNT;

import backend.mulkkam.common.exception.CommonException;

public record IntakeAmount(
        int value
) {

    private static final int MIN_INTAKE_AMOUNT = 1;
    private static final int MAX_INTAKE_AMOUNT = 2_000;

    public IntakeAmount {
        if (value > MAX_INTAKE_AMOUNT || value < MIN_INTAKE_AMOUNT) {
            throw new CommonException(INVALID_INTAKE_AMOUNT);
        }
    }
}
