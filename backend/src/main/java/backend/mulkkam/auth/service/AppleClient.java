package backend.mulkkam.auth.service;

import backend.mulkkam.auth.dto.response.AppleTokenResponse;

public interface AppleClient {

    AppleTokenResponse getToken(String authorizationCode, String clientSecret);
}