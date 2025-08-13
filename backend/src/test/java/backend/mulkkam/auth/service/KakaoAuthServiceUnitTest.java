package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KakaoAuthServiceUnitTest {

    @Mock
    private AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Mock
    private KakaoRestClient kakaoRestClient;

    @Mock
    OauthJwtTokenHandler oauthJwtTokenHandler;

    @Mock
    private OauthAccountRepository oauthAccountRepository;

    @InjectMocks
    private KakaoAuthService kakaoAuthService;

    @DisplayName("회원가입 시에")
    @Nested
    class SingIn {

        @DisplayName("회원가입 한 적이 없는 사용자라면 OauthAccount 를 새롭게 저장한다")
        @Test
        void success_withNewOauthAccount() {
            // given
            String accessTokenForKakao = "kakao";
            String oauthId = "temp";

            when(kakaoRestClient.getUserInfo(accessTokenForKakao))
                    .thenReturn(new KakaoUserInfo(oauthId));

            when(oauthAccountRepository.findByOauthId(oauthId))
                    .thenReturn(java.util.Optional.empty());

            when(oauthAccountRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            when(oauthJwtTokenHandler.createAccessToken(any())).thenReturn("AT");
            when(oauthJwtTokenHandler.createRefreshToken(any())).thenReturn("RT");

            KakaoSigninRequest kakaoSigninRequest = new KakaoSigninRequest(accessTokenForKakao);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            verify(oauthAccountRepository).save(any());
            verify(accountRefreshTokenRepository).save(any());
        }

        @DisplayName("회원가입을 했던 사용자라면 저장을 하지 않는다")
        @Test
        void success_withAlreadySavedOauthdAccount() {
            // given
            String accessTokenForKakao = "kakao";
            String oauthId = "temp";

            when(kakaoRestClient.getUserInfo(accessTokenForKakao))
                    .thenReturn(new KakaoUserInfo(oauthId));

            OauthAccount existing =
                    new OauthAccount(oauthId, OauthProvider.KAKAO);

            when(oauthAccountRepository.findByOauthId(oauthId))
                    .thenReturn(java.util.Optional.of(existing));

            when(oauthJwtTokenHandler.createAccessToken(any())).thenReturn("AT");
            when(oauthJwtTokenHandler.createRefreshToken(any())).thenReturn("RT");

            KakaoSigninRequest kakaoSigninRequest = new KakaoSigninRequest(accessTokenForKakao);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            verify(oauthAccountRepository, never()).save(any());
        }

        @DisplayName("리프레시 토큰이 발급되며 저장된다")
        @Test
        void success_saveRefreshToken() {
            // given
            String accessTokenForKakao = "kakao";
            String oauthId = "temp";

            when(kakaoRestClient.getUserInfo(accessTokenForKakao))
                    .thenReturn(new KakaoUserInfo(oauthId));

            OauthAccount existing =
                    new OauthAccount(oauthId, OauthProvider.KAKAO);

            when(oauthAccountRepository.findByOauthId(oauthId))
                    .thenReturn(java.util.Optional.of(existing));

            when(oauthJwtTokenHandler.createAccessToken(any())).thenReturn("AT");

            String refreshToken = "RT";
            when(oauthJwtTokenHandler.createRefreshToken(any())).thenReturn(refreshToken);

            KakaoSigninRequest kakaoSigninRequest = new KakaoSigninRequest(accessTokenForKakao);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            verify(accountRefreshTokenRepository).save(any());
        }

        @DisplayName("이미 리프레시 토큰이 데이터베이스 상 존재하는 사용자라면 내용을 업데이트한다")
        @Test
        void success_alreadyExistingAccountRefreshToken() {
            // given
            String accessTokenForKakao = "kakao";
            String oauthId = "temp";

            when(kakaoRestClient.getUserInfo(accessTokenForKakao))
                    .thenReturn(new KakaoUserInfo(oauthId));

            OauthAccount existingOauthAccount =
                    new OauthAccount(oauthId, OauthProvider.KAKAO);

            when(oauthAccountRepository.findByOauthId(oauthId))
                    .thenReturn(java.util.Optional.of(existingOauthAccount));

            when(oauthJwtTokenHandler.createAccessToken(any())).thenReturn("AT");

            String refreshToken = "RT";
            when(oauthJwtTokenHandler.createRefreshToken(any())).thenReturn(refreshToken);

            AccountRefreshToken existingAccountRefreshToken = mock(AccountRefreshToken.class);
            when(accountRefreshTokenRepository.findByAccount(existingOauthAccount))
                    .thenReturn(java.util.Optional.of(existingAccountRefreshToken));

            KakaoSigninRequest kakaoSigninRequest = new KakaoSigninRequest(accessTokenForKakao);

            // when
            kakaoAuthService.signIn(kakaoSigninRequest);

            // then
            verify(accountRefreshTokenRepository, never()).save(any());
            verify(existingAccountRefreshToken).reissueToken(refreshToken);
        }
    }
}
