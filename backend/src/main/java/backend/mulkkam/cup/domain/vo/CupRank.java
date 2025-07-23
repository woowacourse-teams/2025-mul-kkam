package backend.mulkkam.cup.domain.vo;

public record CupRank(
        Integer value
) {

    private static final int MAX_CUP_COUNT = 3;
    private static final int CUP_RANK_OFFSET = 1;

    public CupRank {
        if (value > MAX_CUP_COUNT) {
            throw new IllegalArgumentException("컵은 최대 3개까지 등록 가능합니다.");
        }
    }

    public CupRank nextRank() {
        return new CupRank(this.value + CUP_RANK_OFFSET);
    }
}
