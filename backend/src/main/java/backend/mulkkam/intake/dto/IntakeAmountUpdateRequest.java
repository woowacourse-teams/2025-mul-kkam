package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeAmountUpdateRequest(
        int amount
) {
    public Amount toAmount() {
        return new Amount(this.amount);
    }
}
