package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.TargetAmount;

public record AchievementRate(double value) {

    private static final double DEFAULT_VALUE = 0;
    private static final int INVALID_DIVISOR = 0;
    private static final double MAX_VALUE = 100;

    public AchievementRate(int totalIntakeAmount, TargetAmount targetAmount) {
        this(computeRate(totalIntakeAmount, targetAmount));
    }

    private static double computeRate(int totalIntakeAmount, TargetAmount targetAmount) {
        int target = targetAmount.value();
        if (target == INVALID_DIVISOR) {
            return DEFAULT_VALUE;
        }

        double value = ((double) totalIntakeAmount / target) * 100;
        return Math.min(MAX_VALUE, value);
    }

    public static AchievementRate empty() {
        return new AchievementRate(DEFAULT_VALUE);
    }
}
