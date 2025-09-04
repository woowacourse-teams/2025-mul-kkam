package backend.mulkkam.support.fixture.cup.dto;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.dto.request.CreateCupRequest;

public class CreateCupRequestFixtureBuilder {

    private final Long cupEmojiId;
    private String cupNickname;
    private Integer cupAmount;
    private Integer cupRank;
    private String intakeType;

    private CreateCupRequestFixtureBuilder(Long cupEmojiId) {
        this.cupNickname = "우테코 텀블러";
        this.cupAmount = 1_500;
        this.cupRank = 1;
        this.intakeType = IntakeType.WATER.name();
        this.cupEmojiId = cupEmojiId;
    }

    public static CreateCupRequestFixtureBuilder withCupEmojiId(Long cupEmojiId) {
        return new CreateCupRequestFixtureBuilder(cupEmojiId);
    }

    public CreateCupRequestFixtureBuilder cupNickname(String cupNickname) {
        this.cupNickname = cupNickname;
        return this;
    }

    public CreateCupRequestFixtureBuilder cupAmount(Integer cupAmount) {
        this.cupAmount = cupAmount;
        return this;
    }

    public CreateCupRequestFixtureBuilder cupRank(Integer cupRank) {
        this.cupRank = cupRank;
        return this;
    }

    public CreateCupRequestFixtureBuilder intakeType(IntakeType intakeType) {
        this.intakeType = intakeType.name();
        return this;
    }

    public CreateCupRequest build() {
        return new CreateCupRequest(
                this.cupNickname,
                this.cupAmount,
                this.cupRank,
                this.intakeType,
                this.cupEmojiId
        );
    }
}
