package backend.mulkkam.intake.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;

import backend.mulkkam.common.exception.CommonException;

public record Amount(
        int value
) {
    public Amount {
        if (value <= 0 || value >= 10000) {
            throw new CommonException(INVALID_AMOUNT);
        }
    }
}
