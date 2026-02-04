package backend.mulkkam.auth.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.REQUEST_CONFLICT;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OauthAccountService {

    private final OauthJwtTokenHandler jwtTokenHandler;
    private final OauthAccountRepository oauthAccountRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Transactional
    public OauthLoginResponse findOrCreateAndIssueTokens(String oauthId, OauthProvider provider, String deviceUuid) {
        OauthAccount oauthAccount = oauthAccountRepository
            .findByOauthIdAndOauthProvider(oauthId, provider)
            .orElseGet(() -> createSafely(oauthId, provider));

        String accessToken = jwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);
        String refreshToken = updateAccountRefreshToken(
            oauthAccount,
            jwtTokenHandler.createRefreshToken(oauthAccount, deviceUuid),
            deviceUuid
        );

        return new OauthLoginResponse(accessToken, refreshToken, oauthAccount.finishedOnboarding());
    }

    private OauthAccount createSafely(String oauthId, OauthProvider provider) {
        try {
            return oauthAccountRepository.saveAndFlush(new OauthAccount(oauthId, provider));
        } catch (DataIntegrityViolationException e) {
            return oauthAccountRepository
                .findByOauthIdAndOauthProvider(oauthId, provider)
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

        AccountRefreshToken accountRefreshToken = new AccountRefreshToken(oauthAccount, newRefreshToken, deviceUuid);
        try {
            AccountRefreshToken savedPersistedToken = accountRefreshTokenRepository.saveAndFlush(accountRefreshToken);
            return savedPersistedToken.getRefreshToken();
        } catch (DataIntegrityViolationException e) {
            return accountRefreshTokenRepository.findByAccountAndDeviceUuid(oauthAccount, deviceUuid)
                .orElseThrow(() -> new CommonException(REQUEST_CONFLICT))
                .getRefreshToken();
        }
    }
}
