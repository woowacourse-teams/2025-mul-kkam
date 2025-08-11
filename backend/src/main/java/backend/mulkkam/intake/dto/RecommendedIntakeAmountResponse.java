package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.Amount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "권장 음수량 응답")
public record RecommendedIntakeAmountResponse(
        @Schema(description = "권장 음수량 (ml)", example = "2000", minimum = "1")
        int amount
) {

    public RecommendedIntakeAmountResponse(Amount amount) {
        this(amount.value());
    }
}
