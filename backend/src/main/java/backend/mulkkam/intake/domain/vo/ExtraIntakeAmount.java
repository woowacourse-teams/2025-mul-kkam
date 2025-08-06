package backend.mulkkam.intake.domain.vo;

public record ExtraIntakeAmount(
        double value
) {
    private static final int EXTRA_INTAKE_TEMPERATURE_THRESHOLD = 26;
    private static final int WATER_INTAKE_COEFFICIENT_PER_DEGREE = 5;

    public static ExtraIntakeAmount calculateWithAverageTemperature(
            double averageTemperature,
            double weight
    ) {
        if (averageTemperature <= EXTRA_INTAKE_TEMPERATURE_THRESHOLD) {
            return new ExtraIntakeAmount(0);
        }
        double differenceBetweenThreshold = averageTemperature - EXTRA_INTAKE_TEMPERATURE_THRESHOLD;
        return new ExtraIntakeAmount(
                weight * WATER_INTAKE_COEFFICIENT_PER_DEGREE * differenceBetweenThreshold);
    }
}
