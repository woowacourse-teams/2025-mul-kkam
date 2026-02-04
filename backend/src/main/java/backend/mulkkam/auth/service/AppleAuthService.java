package backend.mulkkam.auth.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.REQUEST_CONFLICT;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.AppleSignInRequest;
import backend.mulkkam.auth.dto.response.AppleTokenResponse;
import backend.mulkkam.auth.dto.response.AppleUserInfo;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.AppleClientSecretGenerator;
import backend.mulkkam.auth.infrastructure.AppleIdTokenParser;
import backend.mulkkam.auth.infrastructure.AppleRestClient;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final OauthJwtTokenHandler jwtTokenHandler;
    private final OauthAccountRepository oauthAccountRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Transactional
    public OauthLoginResponse signIn(AppleSignInRequest request) {
        // 1. Client Secret 생성 (ES256 JWT)
        String clientSecret = clientSecretGenerator.generate();

        // 2. Apple Token API 호출
        AppleTokenResponse tokenResponse = appleRestClient.getToken(
                request.authorizationCode(),
                clientSecret
        );

        // 3. id_token 파싱하여 사용자 정보 추출
        AppleUserInfo userInfo = idTokenParser.parse(tokenResponse.idToken());
        String oauthId = userInfo.appleUserId();

        // 4. OauthAccount 조회 또는 생성
        OauthAccount oauthAccount = oauthAccountRepository
                .findByOauthIdAndOauthProvider(oauthId, APPLE_OAUTH_PROVIDER)
                .orElseGet(() -> createSafely(oauthId));

        // 5. 애플리케이션 토큰 발급
        String accessToken = jwtTokenHandler.createAccessToken(oauthAccount, request.deviceUuid());
        String refreshToken = updateAccountRefreshToken(
                oauthAccount,
                jwtTokenHandler.createRefreshToken(oauthAccount, request.deviceUuid()),
                request.deviceUuid()
        );

        return new OauthLoginResponse(accessToken, refreshToken, oauthAccount.finishedOnboarding());
    }

    private OauthAccount createSafely(String oauthId) {
        try {
            return oauthAccountRepository.saveAndFlush(new OauthAccount(oauthId, APPLE_OAUTH_PROVIDER));
        } catch (DataIntegrityViolationException e) {
            return oauthAccountRepository
                    .findByOauthIdAndOauthProvider(oauthId, APPLE_OAUTH_PROVIDER)
                    .orElseThrow(() -> e);
        }
    }

    private String updateAccountRefreshToken(
            OauthAccount oauthAccount,
            String newRefreshToken,
            String deviceUuid
    ) {
        Optional<AccountRefreshToken> foundRefreshToken =
                accountRefreshTokenRepository.findByAccountAndDeviceUuid(oauthAccount, deviceUuid);

        if (foundRefreshToken.isPresent()) {
            AccountRefreshToken existingToken = foundRefreshToken.get();
            existingToken.reissueToken(newRefreshToken);
            return existingToken.getRefreshToken();
        }

        AccountRefreshToken candidateToken = new AccountRefreshToken(oauthAccount, newRefreshToken, deviceUuid);
        try {
            AccountRefreshToken persistedToken = accountRefreshTokenRepository.saveAndFlush(candidateToken);
            return persistedToken.getRefreshToken();
        } catch (DataIntegrityViolationException e) {
            return accountRefreshTokenRepository.findByAccountAndDeviceUuid(oauthAccount, deviceUuid)
                    .orElseThrow(() -> new CommonException(REQUEST_CONFLICT))
                    .getRefreshToken();
        }
    }
}