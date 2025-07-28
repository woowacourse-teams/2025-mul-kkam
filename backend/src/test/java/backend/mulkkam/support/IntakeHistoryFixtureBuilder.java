package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IntakeHistoryFixtureBuilder {

    private final Member member;
    private LocalDateTime dateTime = LocalDateTime.of(
            LocalDate.of(2025, 3, 19),
            LocalTime.of(15, 30, 30)
    );
    private Amount intakeAmount = new Amount(500);
    private Amount targetIntakeAmount = new Amount(1_000);

    private IntakeHistoryFixtureBuilder(Member member) {
        this.member = member;
    }

    public static IntakeHistoryFixtureBuilder withMember(Member member) {
        return new IntakeHistoryFixtureBuilder(member);
    }

    public IntakeHistoryFixtureBuilder intakeAmount(Amount intakeAmount) {
        this.intakeAmount = intakeAmount;
        return this;
    }

    public IntakeHistoryFixtureBuilder targetIntakeAmount(Amount targetIntakeAmount) {
        this.targetIntakeAmount = targetIntakeAmount;
        return this;
    }

    public IntakeHistoryFixtureBuilder dateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public IntakeHistory build() {
        return new IntakeHistory(
                this.member,
                this.dateTime,
                this.intakeAmount,
                this.targetIntakeAmount
        );
    }
}
