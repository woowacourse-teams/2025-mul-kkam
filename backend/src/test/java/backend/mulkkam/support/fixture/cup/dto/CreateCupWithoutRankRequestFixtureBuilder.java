package backend.mulkkam.support.fixture.cup.dto;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.dto.request.CreateCupWithoutRankRequest;

public class CreateCupWithoutRankRequestFixtureBuilder {

    private final Long cupEmojiId;
    private String cupNickname;
    private Integer cupAmount;
    private IntakeType intakeType;

    private CreateCupWithoutRankRequestFixtureBuilder(Long cupEmojiId) {
        this.cupNickname = "선물받은 텀블러";
        this.cupAmount = 1_500;
        this.intakeType = IntakeType.WATER;
        this.cupEmojiId = cupEmojiId;
    }

    public static CreateCupWithoutRankRequestFixtureBuilder withCupEmojiId(Long cupEmojiId) {
        return new CreateCupWithoutRankRequestFixtureBuilder(cupEmojiId);
    }

    public CreateCupWithoutRankRequestFixtureBuilder cupNickname(String cupNickname) {
        this.cupNickname = cupNickname;
        return this;
    }

    public CreateCupWithoutRankRequestFixtureBuilder cupAmount(Integer cupAmount) {
        this.cupAmount = cupAmount;
        return this;
    }

    public CreateCupWithoutRankRequestFixtureBuilder intakeType(IntakeType intakeType) {
        this.intakeType = intakeType;
        return this;
    }

    public CreateCupWithoutRankRequest build() {
        return new CreateCupWithoutRankRequest(
                this.cupNickname,
                this.cupAmount,
                this.intakeType,
                this.cupEmojiId
        );
    }
}
