package backend.mulkkam.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppleOauthConfig {

    @Value("${oauth.apple.team-id}")
    private String teamId;

    @Value("${oauth.apple.client-id}")
    private String clientId;

    @Value("${oauth.apple.key-id}")
    private String keyId;

    @Value("${oauth.apple.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.apple.auth-url}")
    private String authUrl;

    @Value("${oauth.apple.private-key-path}")
    private String privateKeyPath;
}