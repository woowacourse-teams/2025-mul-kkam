package backend.mulkkam.intake.dto.response;

import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 맞춤 권장 음수량 응답")
public record IntakeRecommendedAmountResponse(
        @Schema(description = "권장 음수량 (ml)", example = "5000", minimum = "200")
        int amount
) {

    public IntakeRecommendedAmountResponse(TargetAmount amount) {
        this(amount.value());
    }
}
