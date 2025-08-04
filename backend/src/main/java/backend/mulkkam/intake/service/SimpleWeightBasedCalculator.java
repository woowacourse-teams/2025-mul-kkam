package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public class SimpleWeightBasedCalculator implements IntakeAmountCalculator{

    private static final int WATER_INTAKE_PER_KG = 30;

    @Override
    public Amount calculate(PhysicalAttributes physicalAttributes) {
        return new Amount((int) (physicalAttributes.getWeight() * WATER_INTAKE_PER_KG));
    }
}
