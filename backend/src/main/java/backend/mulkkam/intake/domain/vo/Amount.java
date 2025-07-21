package backend.mulkkam.intake.domain.vo;

public record Amount(
        int value
) {
    public Amount {
        if (value < 0) {
            throw new IllegalArgumentException("용량은 음수가 될 수 없습니다.");
        }
    }
}
