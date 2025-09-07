package backend.mulkkam.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogoutRequest(
        @Schema(description = "디바이스 uuid", example = "deviceUuid")
        String deviceUuid
) {
}
