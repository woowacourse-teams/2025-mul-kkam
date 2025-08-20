package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;

public class CupFixtureBuilder {

    private final Member member;
    private final CupEmoji cupEmoji;
    private CupNickname cupNickname = new CupNickname("스타벅스");
    private CupAmount cupAmount = new CupAmount(500);
    private CupRank cupRank = new CupRank(1);
    private IntakeType intakeType = IntakeType.WATER;

    private CupFixtureBuilder(Member member) {
        this.member = member;
    }

    public static CupFixtureBuilder withMemberAndCupEmoji(Member member, CupEmoji cupEmoji) {
        return new CupFixtureBuilder(member, cupEmoji);
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

    public CupFixtureBuilder intakeType(IntakeType intakeType) {
        this.intakeType = intakeType;
        return this;
    }

    public Cup build() {
        return new Cup(
                member,
                cupNickname,
                cupAmount,
                cupRank,
                intakeType,
                cupEmoji
        );
    }

    public Cup buildWithId(Long id) {
        return new Cup(
                id,
                member,
                cupNickname,
                cupAmount,
                cupRank,
                intakeType,
                cupEmoji
        );
    }
}
