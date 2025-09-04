package backend.mulkkam.auth.dto.response;

public record ReissueTokenResponse(
        String accessToken,
        String refreshToken
) {
}
