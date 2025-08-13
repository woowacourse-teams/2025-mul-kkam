package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record RecommendAmount(int value) {

    private static final int WATER_INTAKE_PER_KG = 30;
    private static final int MIN_RECOMMEND_AMOUNT = 200;
    private static final int MAX_RECOMMEND_AMOUNT = 5_000;

    public RecommendAmount(PhysicalAttributes physicalAttributes) {
        this(calculateRecommendedAmount((int) (physicalAttributes.getWeight() * WATER_INTAKE_PER_KG)));
    }

    public RecommendAmount(int value) {
        this.value = calculateRecommendedAmount(value);
    }

    private static int calculateRecommendedAmount(int value) {
        if (value < MIN_RECOMMEND_AMOUNT) {
            return MIN_RECOMMEND_AMOUNT;
        }
        return Math.min(value, MAX_RECOMMEND_AMOUNT);
    }
}
