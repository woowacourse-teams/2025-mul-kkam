package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaterIntakeAmountCalculator {

    private static final int WEIGHT_CONVERSION_FACTOR = 30;

    private final IntakeCondition intakeCondition;

    public Amount calculate() {
        return new Amount((int) (intakeCondition.getWeight() * WEIGHT_CONVERSION_FACTOR));
    }
}
