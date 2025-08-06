package backend.mulkkam.support;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public class MemberFixtureBuilder {

    private MemberNickname memberNickname;
    private Gender gender;
    private Double weight;
    private PhysicalAttributes physicalAttributes;
    private Amount targetAmount;
    private boolean isMarketingNotificationAgreed;
    private boolean isNightNotificationAgreed;

    private MemberFixtureBuilder() {
        this.memberNickname = new MemberNickname("히로");
        this.gender = Gender.FEMALE;
        this.weight = 50.2;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        this.targetAmount = new Amount(1_000);
    }

    public static MemberFixtureBuilder builder() {
        return new MemberFixtureBuilder();
    }

    public MemberFixtureBuilder memberNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
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

    public MemberFixtureBuilder targetAmount(Amount targetAmount) {
        this.targetAmount = targetAmount;
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

    public Member build() {
        return new Member(
                this.memberNickname,
                this.physicalAttributes,
                this.targetAmount,
                this.isMarketingNotificationAgreed,
                this.isNightNotificationAgreed
        );
    }

    public Member buildWithId(Long id) {
        return new Member(
                id,
                this.memberNickname,
                this.physicalAttributes,
                this.targetAmount,
                this.isMarketingNotificationAgreed,
                this.isNightNotificationAgreed
        );
    }
}
