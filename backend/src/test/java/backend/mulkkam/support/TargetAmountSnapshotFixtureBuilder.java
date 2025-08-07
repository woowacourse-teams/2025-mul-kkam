package backend.mulkkam.support;

import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;

public class TargetAmountSnapshotFixtureBuilder {

    private final Member member;
    private LocalDate updatedAt = LocalDate.of(2025, 7, 25);
    private Amount targetAmount = new Amount(500);

    private TargetAmountSnapshotFixtureBuilder(Member member) {
        this.member = member;
    }

    public static TargetAmountSnapshotFixtureBuilder withMember(Member member) {
        return new TargetAmountSnapshotFixtureBuilder(member);
    }

    public TargetAmountSnapshotFixtureBuilder updatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public TargetAmountSnapshotFixtureBuilder targetAmount(Amount targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    public TargetAmountSnapshot build() {
        return new TargetAmountSnapshot(member, updatedAt, targetAmount);
    }
}
