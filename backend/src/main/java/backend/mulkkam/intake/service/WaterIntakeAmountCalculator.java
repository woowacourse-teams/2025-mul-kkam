package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaterIntakeAmountCalculator {

    private static final int WEIGHT_CONVERSION_FACTOR = 30;

    private final RecommendedIntakeCalculateCondition recommendedIntakeCalculateCondition;

    public Amount calculate() {
        return new Amount((int) (recommendedIntakeCalculateCondition.getWeight() * WEIGHT_CONVERSION_FACTOR));
    }
}
