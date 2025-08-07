package backend.mulkkam.auth.dto;

public record OauthLoginResponse(
        String accessToken,
        boolean finishedOnboarding
) {
}
