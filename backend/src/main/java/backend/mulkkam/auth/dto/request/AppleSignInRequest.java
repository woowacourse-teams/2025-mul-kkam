package backend.mulkkam.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AppleSignInRequest(
    @Schema(
        description = "Apple로부터 받은 Authorization Code",
        example = "c1234abcd...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String authorizationCode,

    @Schema(description = "랜덤 uuid")
    String deviceUuid
) {

}
