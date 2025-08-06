package backend.mulkkam.intake.dto.response;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeRecommendedAmountResponse(int amount) {

    public IntakeRecommendedAmountResponse(Amount amount) {
        this(amount.value());
    }
}
