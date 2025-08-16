package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record RecommendAmount(int value) {

    private static final int WATER_INTAKE_PER_KG = 30;
    private static final int MIN_RECOMMEND_AMOUNT = 200;
    private static final int MAX_RECOMMEND_AMOUNT = 5_000;
    public static final int STANDARD_INTAKE_AMOUNT = 1800;

    public RecommendAmount(PhysicalAttributes physicalAttributes) {
        this(calculateRecommendedAmount(physicalAttributes));
    }

    public RecommendAmount(int value) {
        this.value = clampValue(value);
    }

    private static int calculateRecommendedAmount(PhysicalAttributes physicalAttributes) {
        if (physicalAttributes.getWeight() == null) {
            return STANDARD_INTAKE_AMOUNT;
        }
        return clampValue((int) (physicalAttributes.getWeight() * WATER_INTAKE_PER_KG));
    }

    private static int clampValue(int value) {
        if (value < MIN_RECOMMEND_AMOUNT) {
            return MIN_RECOMMEND_AMOUNT;
        }
        return Math.min(value, MAX_RECOMMEND_AMOUNT);
    }
}
