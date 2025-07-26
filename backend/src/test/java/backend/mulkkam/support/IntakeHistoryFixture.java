package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IntakeHistoryFixture {

    private Member member;
    private LocalDateTime dateTime;
    private Amount intakeAmount;
    private Amount targetIntakeAmount;

    private IntakeHistoryFixture() {
        this.dateTime = LocalDateTime.of(
                LocalDate.of(2025, 3, 19),
                LocalTime.of(15, 30, 30)
        );
        this.intakeAmount = new Amount(500);
        this.targetIntakeAmount = new Amount(1_000);
    }

    public static IntakeHistoryFixture builder() {
        return new IntakeHistoryFixture();
    }

    public IntakeHistoryFixture member(Member member) {
        this.member = member;
        return this;
    }

    public IntakeHistoryFixture intakeAmount(Amount intakeAmount) {
        this.intakeAmount = intakeAmount;
        return this;
    }

    public IntakeHistoryFixture targetIntakeAmount(Amount targetIntakeAmount) {
        this.targetIntakeAmount = targetIntakeAmount;
        return this;
    }

    public IntakeHistoryFixture dateTime(LocalDateTime dateTime) {
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
