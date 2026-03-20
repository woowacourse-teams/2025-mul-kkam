package backend.mulkkam.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReissueTokenRequest(
        @Schema(description = "로그인시 발급받은 리프레시 토큰", example = "eyh...")
        String refreshToken,

        @Schema(description = "랜덤 uuid")
        String deviceUuid
) {
}
