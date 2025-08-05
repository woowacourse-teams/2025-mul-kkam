package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;

public record RecommendedIntakeAmountResponse(int amount) {

    public RecommendedIntakeAmountResponse(Amount amount) {
        this(amount.value());
    }
}
