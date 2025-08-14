package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import java.time.LocalTime;

public class IntakeHistoryDetailFixtureBuilder {

    private final IntakeHistory intakeHistory;
    private LocalTime time = LocalTime.of(10, 0, 0);
    private IntakeAmount intakeAmount = new IntakeAmount(1_000);

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

    public IntakeHistoryDetail build() {
        return new IntakeHistoryDetail(
                this.time,
                this.intakeAmount,
                this.intakeHistory
        );
    }
}
