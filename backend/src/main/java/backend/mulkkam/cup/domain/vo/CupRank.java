package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;

import backend.mulkkam.common.exception.CommonException;

public record CupRank(Integer value) implements Comparable<CupRank> {

    private static final int MAX = 3;
    private static final int MIN = 1;

    public CupRank {
        if (value > MAX || value < MIN) {
            throw new CommonException(INVALID_CUP_COUNT);
        }
    }

    public CupRank promote() {
        return new CupRank(value - 1);
    }

    public CupRank demote() {
        return new CupRank(value + 1);
    }

    @Override
    public int compareTo(CupRank other) {
        return value.compareTo(other.value);
    }
}
