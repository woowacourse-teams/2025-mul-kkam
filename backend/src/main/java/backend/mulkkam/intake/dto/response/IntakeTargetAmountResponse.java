package backend.mulkkam.intake.dto.response;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeTargetAmountResponse(int amount) {

    public IntakeTargetAmountResponse(Amount amount) {
        this(amount.value());
    }
}
