package backend.mulkkam.intake.dto.response;

import backend.mulkkam.intake.domain.vo.Amount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 목표 음수량 응답")
public record IntakeTargetAmountResponse(
        @Schema(description = "목표 음수량 (ml)", example = "2000", minimum = "1")
        int amount
) {

    public IntakeTargetAmountResponse(Amount amount) {
        this(amount.value());
    }
}
