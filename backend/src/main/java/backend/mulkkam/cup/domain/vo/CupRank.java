package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_SIZE;

import backend.mulkkam.common.exception.CommonException;

public record CupRank(
        Integer value
) {

    private static final int MAX_CUP_COUNT = 3;
    private static final int MIN_CUP_COUNT = 0;
    private static final int CUP_RANK_OFFSET = 1;

    public CupRank {
        if (value > MAX_CUP_COUNT || value < MIN_CUP_COUNT) {
            throw new CommonException(INVALID_CUP_SIZE);
        }
    }

    public CupRank nextRank() {
        return new CupRank(this.value + CUP_RANK_OFFSET);
    }
}
