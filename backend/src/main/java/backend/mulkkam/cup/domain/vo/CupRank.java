package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_SIZE;

import backend.mulkkam.common.exception.CommonException;

public record CupRank(Integer value) {

    private static final int MAX = 3;
    private static final int MIN = 1;

    public CupRank {
        if (value > MAX || value < MIN) {
            throw new CommonException(INVALID_CUP_SIZE);
        }
    }
}
