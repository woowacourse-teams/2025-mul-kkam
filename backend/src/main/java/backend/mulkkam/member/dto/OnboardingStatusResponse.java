package backend.mulkkam.member.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "온보딩 상태 조회 응답")
public record OnboardingStatusResponse(
        @Parameter(description = "온보딩 완료 여부", example = "true")
        boolean finishedOnboarding
) {
}
