package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;

public class CupFixture {

    private Member member;
    private MemberNickname memberNickname = new MemberNickname("히로");
    private CupNickname cupNickname = new CupNickname("스타벅스");
    private Amount amount = new Amount(500);
    private Integer rank = 1;

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

    public CupFixture rank(Integer rank) {
        this.rank = rank;
        return this;
    }

    public Cup build() {
        return new Cup(
                this.member,
                this.cupNickname,
                this.amount,
                rank
        );
    }
}

