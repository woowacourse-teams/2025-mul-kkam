package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;

public class CupFixture {

    private Member member;
    private CupNickname cupNickname = new CupNickname("스타벅스");
    private Amount amount = new Amount(500);
    private CupRank cupRank = new CupRank(1);

    public CupFixture member(Member member) {
        this.member = member;
        return this;
    }

    public CupFixture cupNickname(CupNickname cupNickname) {
        this.cupNickname = cupNickname;
        return this;
    }

    public CupFixture amount(Amount amount) {
        this.amount = amount;
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
                this.amount,
                cupRank
        );
    }
}

