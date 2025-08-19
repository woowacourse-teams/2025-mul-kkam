package backend.mulkkam.intake.dto.request;

import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날씨 및 운동 기반 목표 음용량 설정 요청")
public record ModifyIntakeTargetAmountByRecommendRequest(
        @Schema(description = "추천 음용량 (ml)", example = "5000", minimum = "200")
        TargetAmount amount
) {
}
