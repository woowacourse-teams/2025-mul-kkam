package backend.mulkkam.auth.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.KakaoSignInRequest;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class KakaoAuthServiceIntegrationTest extends ServiceIntegrationTest {

    @MockitoBean
    KakaoRestClient kakaoRestClient;

    @MockitoBean
    OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private KakaoAuthService kakaoAuthService;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private AccountRefreshTokenRepository accountRefreshTokenRepository;

    @DisplayName("회원가입을 할 때")
    @Nested
    class SignIn {

        @DisplayName("회원가입 한 적이 없는 사용자라면 OauthAccount 를 새롭게 저장한다")
        @Test
        void success_withNewOauthAccount() {
            // given
            String oauthAccessToken = "temp";
            String oauthId = "memberId";
            String deviceUuid = "deviceUuid";
            when(kakaoRestClient.getUserInfo(oauthAccessToken))
                    .thenReturn(new KakaoUserInfo(oauthId));

            KakaoSignInRequest kakaoSigninRequest = new KakaoSignInRequest(oauthAccessToken, deviceUuid);

            String refreshToken = "refreshToken";
            when(oauthJwtTokenHandler.createRefreshToken(any()))
                    .thenReturn(refreshToken);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            List<OauthAccount> oauthAccounts = oauthAccountRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(oauthAccounts).hasSize(1);
                softly.assertThat(oauthAccounts.getFirst().getOauthId()).isEqualTo(oauthId);
            });
        }

        @DisplayName("회원가입을 했던 사용자라면 저장을 하지 않는다")
        @Test
        void success_withAlreadySavedOauthAccount() {
            // given
            String oauthAccessToken = "temp";
            String oauthId = "memberId";

            OauthAccount oauthAccount = new OauthAccount(oauthId, OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            when(kakaoRestClient.getUserInfo(oauthAccessToken))
                    .thenReturn(new KakaoUserInfo(oauthId));

            String refreshToken = "refreshToken";
            String deviceUuid = "deviceUuid";
            when(oauthJwtTokenHandler.createRefreshToken(any()))
                    .thenReturn(refreshToken);

            KakaoSignInRequest kakaoSigninRequest = new KakaoSignInRequest(oauthAccessToken, deviceUuid);

            List<OauthAccount> oauthAccountsBeforeUpdated = oauthAccountRepository.findAll();

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            List<OauthAccount> oauthAccountsAfterUpdated = oauthAccountRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(oauthAccountsAfterUpdated.size()).isEqualTo(oauthAccountsBeforeUpdated.size());
                softly.assertThat(oauthAccountsAfterUpdated.getFirst().getOauthId()).isEqualTo(oauthId);
            });
        }

        @DisplayName("리프레시 토큰이 발급되며 저장된다")
        @Test
        void success_saveRefreshToken() {
            // given
            String oauthAccessToken = "temp";
            String oauthId = "memberId";

            when(kakaoRestClient.getUserInfo(oauthAccessToken))
                    .thenReturn(new KakaoUserInfo(oauthId));

            String refreshToken = "refreshToken";
            String deviceUuid = "deviceUuid";
            when(oauthJwtTokenHandler.createRefreshToken(any()))
                    .thenReturn(refreshToken);

            KakaoSignInRequest kakaoSigninRequest = new KakaoSignInRequest(oauthAccessToken, deviceUuid);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            List<AccountRefreshToken> accountRefreshTokens = accountRefreshTokenRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(accountRefreshTokens.size()).isEqualTo(1);
                softly.assertThat(accountRefreshTokens.getFirst().getRefreshToken()).isEqualTo(refreshToken);
            });
        }

        @DisplayName("이미 리프레시 토큰이 데이터베이스 상 존재하는 사용자라면 내용을 업데이트한다")
        @Test
        void success_alreadyExistingAccountRefreshToken() {
            // given
            String oauthAccessToken = "temp";
            String oauthId = "memberId";
            String deviceUuid = "deviceUuid";

            OauthAccount oauthAccount = new OauthAccount(oauthId, OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            AccountRefreshToken accountRefreshToken = new AccountRefreshToken(oauthAccount, "originRefreshToken",
                    deviceUuid);
            accountRefreshTokenRepository.save(accountRefreshToken);

            when(kakaoRestClient.getUserInfo(oauthAccessToken))
                    .thenReturn(new KakaoUserInfo(oauthId));

            String newRefreshToken = "newRefreshToken";

            when(oauthJwtTokenHandler.createRefreshToken(any()))
                    .thenReturn(newRefreshToken);

            KakaoSignInRequest kakaoSigninRequest = new KakaoSignInRequest(oauthAccessToken, deviceUuid);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            List<AccountRefreshToken> accountRefreshTokens = accountRefreshTokenRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(accountRefreshTokens.size()).isEqualTo(1);
                softly.assertThat(accountRefreshTokens.getFirst().getRefreshToken()).isEqualTo(newRefreshToken);
            });
        }
    }
}
