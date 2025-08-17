package backend.mulkkam.intake.dto;

import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "권장 음용량 응답")
public record RecommendedIntakeAmountResponse(
        @Schema(description = "권장 음용량 (ml)", example = "2000", minimum = "1")
        int amount
) {

    public RecommendedIntakeAmountResponse(TargetAmount amount) {
        this(amount.value());
    }
}
