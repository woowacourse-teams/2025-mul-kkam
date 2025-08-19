package backend.mulkkam.intake.dto.response;

import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 목표 음용량 응답")
public record IntakeTargetAmountResponse(
        @Schema(description = "목표 음용량 (ml)", example = "5000", minimum = "200")
        int amount
) {

    public IntakeTargetAmountResponse(TargetAmount amount) {
        this(amount.value());
    }
}
