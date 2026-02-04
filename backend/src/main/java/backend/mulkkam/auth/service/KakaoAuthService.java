package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.KakaoSignInRequest;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class KakaoAuthService {

    private static final OauthProvider KAKAO_OAUTH_PROVIDER = OauthProvider.KAKAO;

    private final KakaoRestClient kakaoRestClient;
    private final OauthAccountService oauthAccountService;

    @Transactional
    public OauthLoginResponse signIn(KakaoSignInRequest kakaoSigninRequest) {
        KakaoUserInfo userInfo = kakaoRestClient.getUserInfo(kakaoSigninRequest.oauthAccessToken());

        return oauthAccountService.findOrCreateAndIssueTokens(
            userInfo.oauthMemberId(),
            KAKAO_OAUTH_PROVIDER,
            kakaoSigninRequest.deviceUuid()
        );
    }
}
