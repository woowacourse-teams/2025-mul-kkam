package backend.mulkkam.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import java.time.LocalTime;

public class IntakeHistoryDetailFixtureBuilder {

    private final IntakeHistory intakeHistory;
    private final Cup cup;
    private LocalTime time = LocalTime.of(10, 0, 0);
    private IntakeAmount intakeAmount = new IntakeAmount(1_000);
    private IntakeType intakeType = IntakeType.WATER;

    private IntakeHistoryDetailFixtureBuilder(IntakeHistory intakeHistory, Cup cup) {
        this.intakeHistory = intakeHistory;
        this.cup = cup;
    }

    public static IntakeHistoryDetailFixtureBuilder withIntakeHistoryAndCup(IntakeHistory intakeHistory, Cup cup) {
        return new IntakeHistoryDetailFixtureBuilder(intakeHistory, cup);
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

    public IntakeHistoryDetail build() {
        return new IntakeHistoryDetail(
                this.time,
                this.intakeAmount,
                this.intakeHistory,
                this.intakeType,
                this.cup
        );
    }
}
