package backend.mulkkam.member.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;

import backend.mulkkam.common.exception.CommonException;

public record TargetAmount(
        int value
) {

    private static final int MIN_TARGET_AMOUNT = 200;
    private static final int MAX_TARGET_AMOUNT = 5_000;

    public TargetAmount {
        if (value > MAX_TARGET_AMOUNT || value < MIN_TARGET_AMOUNT) {
            throw new CommonException(INVALID_TARGET_AMOUNT);
        }
    }
}
