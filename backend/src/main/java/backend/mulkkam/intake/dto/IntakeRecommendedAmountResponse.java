package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeRecommendedAmountResponse(int amount) {

    public IntakeRecommendedAmountResponse(Amount amount) {
        this(amount.value());
    }
}
