package backend.mulkkam.intake.service;

import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecommendedIntakeCalculateCondition {

    private static final double DEFAULT_WEIGHT = 60.0;

    private final PhysicalAttributes physicalAttributes;

    public Double getWeight() {
        if (physicalAttributes == null || physicalAttributes.getWeight() == null) {
            return DEFAULT_WEIGHT;
        }
        return physicalAttributes.getWeight();
    }
}
