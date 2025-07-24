package backend.mulkkam.intake.domain.vo;

public record AchievementRate(double value) {

    private static final double DEFAULT_VALUE = 0;
    private static final int INVALID_DIVISOR = 0;
    private static final double MAX_VALUE = 100;

    public AchievementRate(Amount totalIntakeAmount, Amount targetAmount) {
        this(computeRate(totalIntakeAmount, targetAmount));
    }

    private static double computeRate(Amount totalIntakeAmount, Amount targetAmount) {
        int target = targetAmount.value();
        if (target == INVALID_DIVISOR) {
            return DEFAULT_VALUE;
        }

        double value = ((double) totalIntakeAmount.value() / target) * 100;
        if (value >= MAX_VALUE) {
            return 100;
        }

        return value;
    }
}
