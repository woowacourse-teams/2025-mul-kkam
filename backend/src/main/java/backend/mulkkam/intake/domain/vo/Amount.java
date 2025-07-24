package backend.mulkkam.intake.domain.vo;

public record Amount(
        int value
) {
    public Amount {
        if (value <= 0) {
            throw new IllegalArgumentException("음수량은 0보다 커야합니다.");
        }
    }
}
