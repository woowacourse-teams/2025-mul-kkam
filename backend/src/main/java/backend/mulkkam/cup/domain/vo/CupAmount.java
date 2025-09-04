package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;

import backend.mulkkam.common.exception.CommonException;

public record CupAmount(Integer value) {

    private static final int MAX_AMOUNT = 2_000;
    private static final int MIN_AMOUNT = 1;

    public CupAmount {
        if (value > MAX_AMOUNT || value < MIN_AMOUNT) {
            throw new CommonException(INVALID_CUP_AMOUNT);
        }
    }
}
