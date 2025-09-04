package backend.mulkkam.intake.dto.request;

import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "목표 음용량 수정 요청")
public record IntakeTargetAmountModifyRequest(
        @Schema(description = "목표 음용량 (ml)", example = "5000", minimum = "200")
        int amount
) {
    public TargetAmount toAmount() {
        return new TargetAmount(this.amount);
    }
}
