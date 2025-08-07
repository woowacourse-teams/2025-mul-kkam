package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.vo.Amount;

public record ModifyIntakeTargetAmountByRecommendRequest(int amount) {
    
    public Amount toAmount() {
        return new Amount(this.amount);
    }
}
