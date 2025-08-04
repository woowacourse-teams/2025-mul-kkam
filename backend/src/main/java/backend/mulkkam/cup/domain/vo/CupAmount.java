package backend.mulkkam.cup.domain.vo;

import backend.mulkkam.common.exception.CommonException;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_CUP_AMOUNT;

public record CupAmount(Integer value) {

    private static final int MAX_AMOUNT = 10_000;
    private static final int MIN_AMOUNT = 1;

    public CupAmount {
        if (value > MAX_AMOUNT || value < MIN_AMOUNT) {
            throw new CommonException(INVALID_CUP_AMOUNT);
        }
    }
}
