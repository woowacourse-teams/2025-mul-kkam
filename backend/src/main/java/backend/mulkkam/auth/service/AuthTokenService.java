package backend.mulkkam.auth.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.REFRESH_TOKEN_ALREADY_USED;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.REFRESH_TOKEN_IS_EXPIRED;
import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.UNAUTHORIZED;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.dto.request.ReissueTokenRequest;
import backend.mulkkam.auth.dto.response.ReissueTokenResponse;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthTokenService {

    private final OauthJwtTokenHandler oauthJwtTokenHandler;
    private final OauthAccountRepository accountRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Transactional
    public void logout(
            OauthAccountDetails accountDetails
    ) {
        accountRefreshTokenRepository.deleteByAccountIdAndDeviceUuid(accountDetails.id(), accountDetails.deviceUuid());
    }

    @Transactional
    public ReissueTokenResponse reissueToken(ReissueTokenRequest request) {
        String refreshToken = request.refreshToken();
        OauthAccount account = getAccountByToken(refreshToken);
        AccountRefreshToken saved = loadRefreshToken(account, request.deviceUuid());
        validateRequestToken(saved, refreshToken);
        String reissuedAccessToken = oauthJwtTokenHandler.createAccessToken(account, request.deviceUuid());
        String reissuedRefreshToken = oauthJwtTokenHandler.createRefreshToken(account, request.deviceUuid());

        saved.reissueToken(reissuedRefreshToken);
        return new ReissueTokenResponse(reissuedAccessToken, reissuedRefreshToken);
    }

    private OauthAccount getAccountByToken(String refreshToken) {
        Long accountId = getAccountId(refreshToken);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new CommonException(UNAUTHORIZED));
    }

    private Long getAccountId(String refreshToken) {
        try {
            return oauthJwtTokenHandler.getAccountId(refreshToken);
        } catch (InvalidTokenException e) {
            throw new CommonException(REFRESH_TOKEN_IS_EXPIRED);
        }
    }

    private AccountRefreshToken loadRefreshToken(
            OauthAccount oauthAccount,
            String deviceUuid
    ) {
        return accountRefreshTokenRepository.findByAccountAndDeviceUuid(oauthAccount, deviceUuid)
                .orElseThrow(() -> new CommonException(UNAUTHORIZED));
    }

    private void validateRequestToken(
            AccountRefreshToken saved,
            String refreshToken
    ) {
        if (!saved.isMatchWith(refreshToken)) {
            throw new CommonException(REFRESH_TOKEN_ALREADY_USED);
        }
    }
}
