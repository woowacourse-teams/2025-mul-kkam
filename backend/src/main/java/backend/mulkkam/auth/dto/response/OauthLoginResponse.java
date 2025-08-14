package backend.mulkkam.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record OauthLoginResponse(
        @Schema(
                description = "애플리케이션 액세스 토큰 - 인증/인가가 필요한 서비스에서 Authorization 헤더에 첨부해야 합니다.",
                example = "v7dm1Q...abc"
        )
        String accessToken,
        @Schema(
                description = "애플리케이션 리프레시 토큰 - 액세스 토큰 재발급이 필요한 경우 사용됩니다.",
                example = "v7dm1Q...abc"
        )
        String refreshToken,
        @Schema(
                description = "온보딩 진행 여부",
                example = "false"
        )
        boolean finishedOnboarding
) {
}
