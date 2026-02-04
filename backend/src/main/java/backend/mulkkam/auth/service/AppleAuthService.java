package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.AppleSignInRequest;
import backend.mulkkam.auth.dto.response.AppleTokenResponse;
import backend.mulkkam.auth.dto.response.AppleUserInfo;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.AppleClientSecretGenerator;
import backend.mulkkam.auth.infrastructure.AppleIdTokenParser;
import backend.mulkkam.auth.infrastructure.AppleRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AppleAuthService {

    private static final OauthProvider APPLE_OAUTH_PROVIDER = OauthProvider.APPLE;

    private final AppleRestClient appleRestClient;
    private final AppleClientSecretGenerator clientSecretGenerator;
    private final AppleIdTokenParser idTokenParser;
    private final OauthAccountService oauthAccountService;

    @Transactional
    public OauthLoginResponse signIn(AppleSignInRequest request) {
        String clientSecret = clientSecretGenerator.generate();

        AppleTokenResponse tokenResponse = appleRestClient.getToken(
            request.authorizationCode(),
            clientSecret
        );

        AppleUserInfo userInfo = idTokenParser.parse(tokenResponse.idToken());

        return oauthAccountService.findOrCreateAndIssueTokens(
            userInfo.appleUserId(),
            APPLE_OAUTH_PROVIDER,
            request.deviceUuid()
        );
    }
}
