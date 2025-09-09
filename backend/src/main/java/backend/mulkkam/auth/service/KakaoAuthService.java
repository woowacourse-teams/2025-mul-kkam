package backend.mulkkam.auth.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.REQUEST_CONFLICT;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.KakaoSignInRequest;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
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
public class KakaoAuthService {

    private final KakaoRestClient kakaoRestClient;
    private final OauthJwtTokenHandler jwtTokenHandler;
    private final OauthAccountRepository oauthAccountRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Transactional
    public OauthLoginResponse signIn(KakaoSignInRequest kakaoSigninRequest) {
        KakaoUserInfo userInfo = kakaoRestClient.getUserInfo(kakaoSigninRequest.oauthAccessToken());

        String oauthId = userInfo.oauthMemberId();
        OauthAccount oauthAccount = oauthAccountRepository.findByOauthId(oauthId)
                .orElseGet(() -> oauthAccountRepository.save(new OauthAccount(oauthId, OauthProvider.KAKAO)));

        String accessToken = jwtTokenHandler.createAccessToken(oauthAccount);
        String refreshToken = updateAccountRefreshToken(oauthAccount, jwtTokenHandler.createRefreshToken(oauthAccount),
                kakaoSigninRequest.deviceUuid());

        return new OauthLoginResponse(accessToken, refreshToken, oauthAccount.finishedOnboarding());
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
            AccountRefreshToken persistedToken = accountRefreshTokenRepository.save(candidateToken);
            return persistedToken.getRefreshToken();
        } catch (DataIntegrityViolationException e) {
            return accountRefreshTokenRepository.findByAccountAndDeviceUuid(oauthAccount, deviceUuid)
                    .orElseThrow(() -> new CommonException(REQUEST_CONFLICT))
                    .getRefreshToken();
        }
    }
}
