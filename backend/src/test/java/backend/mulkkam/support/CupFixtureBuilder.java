package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;

public class CupFixtureBuilder {

    private Member member;
    private CupNickname cupNickname;
    private CupAmount cupAmount;
    private CupRank cupRank;

    private CupFixtureBuilder() {
        this.cupNickname = new CupNickname("스타벅스");
        this.cupAmount = new CupAmount(500);
        this.cupRank = new CupRank(1);
    }

    public static CupFixtureBuilder builder() {
        return new CupFixtureBuilder();
    }

    public CupFixtureBuilder member(Member member) {
        this.member = member;
        return this;
    }

    public CupFixtureBuilder cupNickname(CupNickname cupNickname) {
        this.cupNickname = cupNickname;
        return this;
    }

    public CupFixtureBuilder cupAmount(CupAmount cupAmount) {
        this.cupAmount = cupAmount;
        return this;
    }

    public CupFixtureBuilder cupRank(CupRank cupRank) {
        this.cupRank = cupRank;
        return this;
    }

    public Cup build() {
        return new Cup(member, cupNickname, cupAmount, cupRank);
    }
}
