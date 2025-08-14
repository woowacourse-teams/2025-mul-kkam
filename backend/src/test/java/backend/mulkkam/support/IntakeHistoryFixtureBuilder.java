package backend.mulkkam.support;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;

public class IntakeHistoryFixtureBuilder {

    private final Member member;
    private LocalDate date = LocalDate.of(2025, 3, 19);
    private TargetAmount targetIntakeAmount = new TargetAmount(1_000);
    private int streak = 5;

    private IntakeHistoryFixtureBuilder(Member member) {
        this.member = member;
    }

    public static IntakeHistoryFixtureBuilder withMember(Member member) {
        return new IntakeHistoryFixtureBuilder(member);
    }

    public IntakeHistoryFixtureBuilder targetIntakeAmount(TargetAmount targetIntakeAmount) {
        this.targetIntakeAmount = targetIntakeAmount;
        return this;
    }

    public IntakeHistoryFixtureBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public IntakeHistoryFixtureBuilder streak(int streak) {
        this.streak = streak;
        return this;
    }

    public IntakeHistory build() {
        return new IntakeHistory(
                this.member,
                this.date,
                this.targetIntakeAmount,
                this.streak
        );
    }
}
