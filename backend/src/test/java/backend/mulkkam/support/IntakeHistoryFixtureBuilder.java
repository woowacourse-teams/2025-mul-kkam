package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;

public class IntakeHistoryFixtureBuilder {

    private final Member member;
    private LocalDate date = LocalDate.of(2025, 3, 19);
    private Amount targetIntakeAmount = new Amount(1_000);

    private IntakeHistoryFixtureBuilder(Member member) {
        this.member = member;
    }

    public static IntakeHistoryFixtureBuilder withMember(Member member) {
        return new IntakeHistoryFixtureBuilder(member);
    }

    public IntakeHistoryFixtureBuilder targetIntakeAmount(Amount targetIntakeAmount) {
        this.targetIntakeAmount = targetIntakeAmount;
        return this;
    }

    public IntakeHistoryFixtureBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public IntakeHistory build() {
        return new IntakeHistory(
                this.member,
                this.date,
                this.targetIntakeAmount
        );
    }
}
