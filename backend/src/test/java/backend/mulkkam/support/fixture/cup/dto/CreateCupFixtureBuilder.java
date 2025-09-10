package backend.mulkkam.support.fixture.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CreateCup;

public class CreateCupFixtureBuilder {

    private final CupEmoji cupEmoji;
    private CupNickname cupNickname;
    private CupAmount cupAmount;
    private CupRank cupRank;
    private IntakeType intakeType;

    private CreateCupFixtureBuilder(CupEmoji cupEmoji) {
        this.cupNickname = new CupNickname("우테코 텀블러");
        this.cupAmount = new CupAmount(1_500);
        this.cupRank = new CupRank(1);
        this.intakeType = IntakeType.WATER;
        this.cupEmoji = cupEmoji;
    }

    private CreateCupFixtureBuilder(Cup cup) {
        this.cupNickname =  cup.getNickname();
        this.cupAmount = cup.getCupAmount();
        this.cupRank = cup.getCupRank();
        this.intakeType = cup.getIntakeType();
        this.cupEmoji = cup.getCupEmoji();
    }

    public static CreateCupFixtureBuilder withCupEmoji(CupEmoji cupEmoji) {
        return new CreateCupFixtureBuilder(cupEmoji);
    }

    public static CreateCupFixtureBuilder withCup(Cup cup) {
        return new CreateCupFixtureBuilder(cup);
    }

    public CreateCupFixtureBuilder cupNickname(String cupNickname) {
        this.cupNickname = new CupNickname(cupNickname);
        return this;
    }

    public CreateCupFixtureBuilder cupAmount(Integer cupAmount) {
        this.cupAmount = new CupAmount(cupAmount);
        return this;
    }

    public CreateCupFixtureBuilder cupRank(Integer cupRank) {
        this.cupRank = new CupRank(cupRank);
        return this;
    }

    public CreateCupFixtureBuilder intakeType(IntakeType intakeType) {
        this.intakeType = intakeType;
        return this;
    }

    public CreateCup build() {
        return new CreateCup(
                this.cupNickname,
                this.cupAmount,
                this.cupRank,
                this.intakeType,
                this.cupEmoji
        );
    }
}
