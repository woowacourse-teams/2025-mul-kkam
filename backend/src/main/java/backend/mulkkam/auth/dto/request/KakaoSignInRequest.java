package backend.mulkkam.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record KakaoSignInRequest(
        @Schema(
                description = "카카오 API로부터 응답받은 access token 값",
                example = "v7dm1Q...abc",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String oauthAccessToken,

        @Schema(description = "디바이스 uuid")
        String deviceUuid
) {
}
