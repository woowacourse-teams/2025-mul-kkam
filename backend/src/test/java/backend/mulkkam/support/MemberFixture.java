package backend.mulkkam.support;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public class MemberFixture {

    private MemberNickname memberNickname;
    private Gender gender;
    private Double weight;
    private PhysicalAttributes physicalAttributes;
    private Amount targetAmount;

    private MemberFixture() {
        this.memberNickname = new MemberNickname("히로");
        this.gender = Gender.FEMALE;
        this.weight = 50.2;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        this.targetAmount = new Amount(1_000);
    }

    public static MemberFixture builder() {
        return new MemberFixture();
    }

    public MemberFixture memberNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
        return this;
    }

    public MemberFixture gender(Gender gender) {
        this.gender = gender;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        return this;
    }

    public MemberFixture weight(Double weight) {
        this.weight = weight;
        this.physicalAttributes = new PhysicalAttributes(this.gender, this.weight);
        return this;
    }

    public MemberFixture targetAmount(Amount targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    public Member build() {
        return new Member(
                this.memberNickname,
                this.physicalAttributes,
                this.targetAmount
        );
    }
}
