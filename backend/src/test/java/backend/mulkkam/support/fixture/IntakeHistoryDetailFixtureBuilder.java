package backend.mulkkam.support.fixture;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import java.time.LocalTime;

public class IntakeHistoryDetailFixtureBuilder {

    private final IntakeHistory intakeHistory;
    private LocalTime time = LocalTime.of(10, 0, 0);
    private IntakeAmount intakeAmount = new IntakeAmount(1_000);
    private IntakeType intakeType = IntakeType.WATER;
    private String cupEmojiUrl = "http://example.com";

    private IntakeHistoryDetailFixtureBuilder(IntakeHistory intakeHistory) {
        this.intakeHistory = intakeHistory;
    }

    public static IntakeHistoryDetailFixtureBuilder withIntakeHistory(IntakeHistory intakeHistory) {
        return new IntakeHistoryDetailFixtureBuilder(intakeHistory);
    }

    public IntakeHistoryDetailFixtureBuilder intakeAmount(IntakeAmount intakeAmount) {
        this.intakeAmount = intakeAmount;
        return this;
    }

    public IntakeHistoryDetailFixtureBuilder time(LocalTime time) {
        this.time = time;
        return this;
    }

    public IntakeHistoryDetailFixtureBuilder intakeType(IntakeType intakeType) {
        this.intakeType = intakeType;
        return this;
    }

    public IntakeHistoryDetailFixtureBuilder cupEmojiUrl(String cupEmojiUrl) {
        this.cupEmojiUrl = cupEmojiUrl;
        return this;
    }

    public IntakeHistoryDetail buildWithCup(Cup cup) {
        return new IntakeHistoryDetail(
                this.time,
                this.intakeHistory,
                this.intakeType,
                cup.getCupAmount().value(),
                cup.getCupEmoji().getUrl()
        );
    }

    public IntakeHistoryDetail buildWithInput() {
        return new IntakeHistoryDetail(
                this.time,
                this.intakeHistory,
                this.intakeType,
                this.intakeAmount.value(),
                new CupEmojiUrl(this.cupEmojiUrl)
        );
    }
}
