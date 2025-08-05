package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeDetail;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import java.time.LocalTime;

public class IntakeDetailFixtureBuilder {

    private final IntakeHistory intakeHistory;
    private LocalTime time = LocalTime.of(10, 0, 0);
    private Amount intakeAmount = new Amount(1_000);

    private IntakeDetailFixtureBuilder(IntakeHistory intakeHistory) {
        this.intakeHistory = intakeHistory;
    }

    public static IntakeDetailFixtureBuilder withIntakeHistory(IntakeHistory intakeHistory) {
        return new IntakeDetailFixtureBuilder(intakeHistory);
    }

    public IntakeDetailFixtureBuilder intakeAmount(Amount intakeAmount) {
        this.intakeAmount = intakeAmount;
        return this;
    }

    public IntakeDetailFixtureBuilder time(LocalTime time) {
        this.time = time;
        return this;
    }

    public IntakeDetail build() {
        return new IntakeDetail(
                this.time,
                this.intakeAmount,
                this.intakeHistory
        );
    }
}
