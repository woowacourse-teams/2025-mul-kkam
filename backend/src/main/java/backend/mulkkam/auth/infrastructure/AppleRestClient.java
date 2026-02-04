package backend.mulkkam.auth.infrastructure;

import backend.mulkkam.auth.config.AppleOauthConfig;
import backend.mulkkam.auth.dto.response.AppleTokenResponse;
import backend.mulkkam.auth.service.AppleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AppleRestClient implements AppleClient {

    private final AppleOauthConfig appleOauthConfig;

    private final RestClient restClient = RestClient.builder()
        .build();

    @Override
    public AppleTokenResponse getToken(String authorizationCode, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleOauthConfig.getClientId());
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", appleOauthConfig.getRedirectUri());

        return restClient.post()
            .uri(appleOauthConfig.getAuthUrl())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(AppleTokenResponse.class);
    }
}
