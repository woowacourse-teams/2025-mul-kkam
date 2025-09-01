package backend.mulkkam.support.fixture;

import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;

import java.time.LocalDate;

public class TargetAmountSnapshotFixtureBuilder {

    private final Member member;
    private LocalDate updatedAt = LocalDate.of(2025, 7, 25);
    private TargetAmount targetAmount = new TargetAmount(500);

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

    public TargetAmountSnapshotFixtureBuilder targetAmount(TargetAmount targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    public TargetAmountSnapshot build() {
        return new TargetAmountSnapshot(member, updatedAt, targetAmount);
    }
}
