package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.dto.OauthLoginResponse;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.infrastructure.KakaoRestClient;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class KakaoAuthService {

    private final KakaoRestClient kakaoRestClient;
    private final OauthAccountRepository oauthAccountRepository;

    public OauthLoginResponse signIn(KakaoSigninRequest kakaoSigninRequest) {
        KakaoUserInfo userInfo = kakaoRestClient.getUserInfo(kakaoSigninRequest.oauthAccessToken());

        String oauthId = userInfo.oauthMemberId();
        Optional<OauthAccount> foundOauthAccount = oauthAccountRepository.findByOauthId(oauthId);

        if (foundOauthAccount.isPresent()) {
            // TODO: 액세스 토큰 만들어서 반환
            return new OauthLoginResponse("냥~");
        }

        OauthAccount oauthAccount = new OauthAccount(oauthId, OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        // TODO: 액세스 토큰 만들어서 반환
        return new OauthLoginResponse("멍~");
    }

}
