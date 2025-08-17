package backend.mulkkam.support;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.member.domain.Member;

public class OauthAccountFixtureBuilder {

    private final Member member;
    private String oauthId = "temp";
    private OauthProvider oauthProvider = OauthProvider.KAKAO;

    private OauthAccountFixtureBuilder(Member member) {
        this.member = member;
    }

    public static OauthAccountFixtureBuilder withMember(Member member) {
        return new OauthAccountFixtureBuilder(member);
    }

    public OauthAccountFixtureBuilder oauthId(String oauthId) {
        this.oauthId = oauthId;
        return this;
    }

    public OauthAccountFixtureBuilder oauthProvider(OauthProvider oauthProvider) {
        this.oauthProvider = oauthProvider;
        return this;
    }

    public OauthAccount build() {
        return new OauthAccount(
                member,
                oauthId,
                oauthProvider
        );
    }
}

