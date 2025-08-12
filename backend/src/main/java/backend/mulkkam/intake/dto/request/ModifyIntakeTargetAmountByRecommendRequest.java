package backend.mulkkam.intake.dto.request;

import backend.mulkkam.intake.domain.vo.Amount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날씨 및 운동 기반 목표 음수량 설정 요청")
public record ModifyIntakeTargetAmountByRecommendRequest(
        @Schema(description = "추천 음수량 (ml)", example = "2000", minimum = "1")
        int amount
) {

    public Amount toAmount() {
        return new Amount(this.amount);
    }
}
