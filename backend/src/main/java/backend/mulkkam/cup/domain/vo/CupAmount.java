package backend.mulkkam.cup.domain.vo;

public record CupAmount(
        Integer value
) {

    private static final int MAX_AMOUNT = 10000;
    private static final int MIN_AMOUNT = 0;

    public CupAmount {
        if (value > MAX_AMOUNT || value < MIN_AMOUNT) {
            throw new IllegalArgumentException("컵 용량이 올바르지 않습니다.");
        }
    }
}
