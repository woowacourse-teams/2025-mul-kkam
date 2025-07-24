package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;

public record IntakeAmountResponse(int amount) {

    public IntakeAmountResponse(Amount amount) {
        this(amount.value());
    }
}
