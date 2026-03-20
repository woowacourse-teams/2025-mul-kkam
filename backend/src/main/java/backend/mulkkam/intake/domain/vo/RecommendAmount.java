package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record RecommendAmount(int value) {

    private static final int WATER_INTAKE_PER_KG = 30;
    private static final int MIN_RECOMMEND_AMOUNT = 200;
    private static final int MAX_RECOMMEND_AMOUNT = 5_000;
    private static final int STANDARD_WEIGHT = 60;

    public RecommendAmount(int value) {
        this.value = clampValue(value);
    }

    public RecommendAmount(PhysicalAttributes physicalAttributes) {
        this(calculateRecommendedAmount(physicalAttributes));
    }

    private static int calculateRecommendedAmount(PhysicalAttributes physicalAttributes) {
        double weight = determineWeight(physicalAttributes);
        return (int) (weight * WATER_INTAKE_PER_KG);
    }

    private static double determineWeight(PhysicalAttributes physicalAttributes) {
        if (physicalAttributes.getWeight() == null) {
            return STANDARD_WEIGHT;
        }
        return physicalAttributes.getWeight();
    }

    private static int clampValue(int value) {
        if (value < MIN_RECOMMEND_AMOUNT) {
            return MIN_RECOMMEND_AMOUNT;
        }
        return Math.min(value, MAX_RECOMMEND_AMOUNT);
    }
}
