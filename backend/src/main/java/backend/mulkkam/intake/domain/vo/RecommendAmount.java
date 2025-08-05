package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record RecommendAmount(Amount amount) {

    private static final int WATER_INTAKE_PER_KG = 30;

    public RecommendAmount(PhysicalAttributes physicalAttributes) {
        this(calculateRecommendedAmount(physicalAttributes));
    }

    private static Amount calculateRecommendedAmount(PhysicalAttributes physicalAttributes) {
        double weight = physicalAttributes.getWeight();
        return new Amount((int) (weight * WATER_INTAKE_PER_KG));
    }
}
