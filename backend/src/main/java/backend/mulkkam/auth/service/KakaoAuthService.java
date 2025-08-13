package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.dto.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class KakaoAuthService {

    private final KakaoRestClient kakaoRestClient;
    private final OauthJwtTokenHandler jwtTokenHandler;
    private final OauthAccountRepository oauthAccountRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Transactional
    public OauthLoginResponse signIn(KakaoSigninRequest kakaoSigninRequest) {
        KakaoUserInfo userInfo = kakaoRestClient.getUserInfo(kakaoSigninRequest.oauthAccessToken());

        String oauthId = userInfo.oauthMemberId();
        OauthAccount oauthAccount = getOauthAccount(oauthId);

        String accessToken = jwtTokenHandler.createAccessToken(oauthAccount);
        String refreshToken = jwtTokenHandler.createRefreshToken(oauthAccount);

        AccountRefreshToken accountRefreshToken = new AccountRefreshToken(oauthAccount, refreshToken);
        accountRefreshTokenRepository.save(accountRefreshToken);

        return new OauthLoginResponse(accessToken, refreshToken, oauthAccount.finishedOnboarding());
    }

    @NotNull
    private OauthAccount getOauthAccount(String oauthId) {
        return oauthAccountRepository.findByOauthId(oauthId)
                .orElseGet(() -> oauthAccountRepository.save(new OauthAccount(oauthId, OauthProvider.KAKAO)));
    }
}
