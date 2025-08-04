package backend.mulkkam.cup.domain.vo;

import backend.mulkkam.common.exception.CommonException;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_RANK_VALUE;

public record CupRank(Integer value) {

    private static final int MAX = 3;
    private static final int MIN = 1;

    public CupRank {
        if (value > MAX || value < MIN) {
            throw new CommonException(INVALID_CUP_RANK_VALUE);
        }
    }

    public CupRank promote() {
        return new CupRank(value - 1);
    }

    public CupRank demote() {
        return new CupRank(value + 1);
    }

    public boolean hasLowerPriorityThan(CupRank other) {
        return value > other.value;
    }
}
