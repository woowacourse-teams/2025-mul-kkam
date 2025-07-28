package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;

public class CupFixture {

    private Member member;
    private CupNickname cupNickname = new CupNickname("스타벅스");
    private CupAmount cupAmount = new CupAmount(500);
    private CupRank cupRank = new CupRank(1);

    public CupFixture member(Member member) {
        this.member = member;
        return this;
    }

    public CupFixture cupNickname(CupNickname cupNickname) {
        this.cupNickname = cupNickname;
        return this;
    }

    public CupFixture cupAmount(CupAmount cupAmount) {
        this.cupAmount = cupAmount;
        return this;
    }

    public CupFixture cupRank(CupRank cupRank) {
        this.cupRank = cupRank;
        return this;
    }

    public Cup build() {
        return new Cup(
                this.member,
                this.cupNickname,
                this.cupAmount,
                cupRank
        );
    }
}
