package backend.mulkkam.intake.domain.vo;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;

public record RecommendAmount(TargetAmount amount) {

    private static final int WATER_INTAKE_PER_KG = 30;

    public RecommendAmount(PhysicalAttributes physicalAttributes) {
        this(calculateRecommendedAmount(physicalAttributes));
    }

    private static TargetAmount calculateRecommendedAmount(PhysicalAttributes physicalAttributes) {
        double weight = physicalAttributes.getWeight();
        return new TargetAmount((int) (weight * WATER_INTAKE_PER_KG));
    }
}
