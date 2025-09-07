package backend.mulkkam.support.fixture;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;

public class AccountRefreshTokenFixtureBuilder {

    private final OauthAccount oauthAccount;
    private String refreshToken = "refreshToken";
    private String deviceUuid = "deviceUuid";

    private AccountRefreshTokenFixtureBuilder(OauthAccount oauthAccount) {
        this.oauthAccount = oauthAccount;
    }

    public static AccountRefreshTokenFixtureBuilder withOauthAccount(OauthAccount oauthAccount) {
        return new AccountRefreshTokenFixtureBuilder(oauthAccount);
    }

    public AccountRefreshTokenFixtureBuilder refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public AccountRefreshTokenFixtureBuilder deviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
        return this;
    }

    public AccountRefreshToken build() {
        return new AccountRefreshToken(
                oauthAccount,
                refreshToken,
                deviceUuid
        );
    }
}
