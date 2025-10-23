package backend.mulkkam.support.fixture.member;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;

public class MemberFixtureBuilder {

    private MemberNickname memberNickname;
    private Gender gender;
    private Double weight;
    private PhysicalAttributes physicalAttributes;
    private TargetAmount targetAmount;
    private boolean isMarketingNotificationAgreed;
    private boolean isNightNotificationAgreed;
    private String activeNickname;
    private boolean isReminderEnabled;

    private MemberFixtureBuilder() {
        this.memberNickname = new MemberNickname("히로");
        this.gender = Gender.FEMALE;
        this.weight = 50.2;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        this.targetAmount = new TargetAmount(1_000);
        this.isMarketingNotificationAgreed = false;
        this.isNightNotificationAgreed = false;
        this.activeNickname = "히로";
        this.isReminderEnabled = true;
    }

    public static MemberFixtureBuilder builder() {
        return new MemberFixtureBuilder();
    }

    public MemberFixtureBuilder memberNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
        this.activeNickname = memberNickname.value();
        return this;
    }


    public MemberFixtureBuilder gender(Gender gender) {
        this.gender = gender;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        return this;
    }

    public MemberFixtureBuilder weight(Double weight) {
        this.weight = weight;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        return this;
    }

    public MemberFixtureBuilder targetAmount(int targetAmount) {
        this.targetAmount = new TargetAmount(targetAmount);
        return this;
    }

    public MemberFixtureBuilder isMarketingNotificationAgreed(boolean isMarketingNotificationAgreed) {
        this.isMarketingNotificationAgreed = isMarketingNotificationAgreed;
        return this;
    }

    public MemberFixtureBuilder isNightNotificationAgreed(boolean isNightNotificationAgreed) {
        this.isNightNotificationAgreed = isNightNotificationAgreed;
        return this;
    }

    public MemberFixtureBuilder isReminderEnabled(boolean isReminderEnabled) {
        this.isReminderEnabled = isReminderEnabled;
        return this;
    }

    public Member build() {
        return new Member(
                this.memberNickname,
                this.physicalAttributes,
                this.targetAmount,
                this.isMarketingNotificationAgreed,
                this.isNightNotificationAgreed,
                this.activeNickname,
                this.isReminderEnabled
        );
    }

    public Member buildWithId(Long id) {
        return new Member(
                id,
                this.memberNickname,
                this.physicalAttributes,
                this.targetAmount,
                this.isMarketingNotificationAgreed,
                this.isNightNotificationAgreed,
                this.activeNickname,
                this.isReminderEnabled
        );
    }
}
