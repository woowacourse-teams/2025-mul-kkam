package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.common.exception.CommonException;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;

public record Amount(
        int value
) {
    public Amount {
        if (value <= 0) {
            throw new CommonException(INVALID_AMOUNT);
        }
    }
}
