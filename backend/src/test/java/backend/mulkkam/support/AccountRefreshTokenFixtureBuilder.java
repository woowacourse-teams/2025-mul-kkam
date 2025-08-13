package backend.mulkkam.support;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;

public class AccountRefreshTokenFixtureBuilder {

    private final OauthAccount oauthAccount;
    private String refreshToken;

    private AccountRefreshTokenFixtureBuilder(OauthAccount oauthAccount) {
        this.oauthAccount = oauthAccount;
    }

    public static AccountRefreshTokenFixtureBuilder withMember(OauthAccount oauthAccount) {
        return new AccountRefreshTokenFixtureBuilder(oauthAccount);
    }

    public AccountRefreshTokenFixtureBuilder refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public AccountRefreshToken build() {
        return new AccountRefreshToken(
                oauthAccount,
                refreshToken
        );
    }
}
