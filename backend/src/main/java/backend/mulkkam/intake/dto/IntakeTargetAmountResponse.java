package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeTargetAmountResponse(int amount) {

    public IntakeTargetAmountResponse(Amount amount) {
        this(amount.value());
    }
}
