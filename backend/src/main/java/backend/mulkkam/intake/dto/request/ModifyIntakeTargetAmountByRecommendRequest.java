package backend.mulkkam.intake.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날씨 및 운동 기반 목표 음용량 설정 요청")
public record ModifyIntakeTargetAmountByRecommendRequest(
        @Schema(description = "추천 음용량 (ml)", example = "355")
        int amount
) {
}
