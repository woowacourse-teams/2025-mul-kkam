package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.vo.Amount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "목표 음수량 수정 요청")
public record IntakeTargetAmountModifyRequest(
        @Schema(description = "목표 음수량 (ml)", example = "2000", minimum = "1")
        int amount
) {
    public Amount toAmount() {
        return new Amount(this.amount);
    }
}
