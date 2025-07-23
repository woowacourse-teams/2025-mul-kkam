package backend.mulkkam.support;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.PhysicalAttributes;

public class MemberFixture {

    private MemberNickname memberNickname = new MemberNickname("히로");
    private Gender gender = Gender.FEMALE;
    private Double weight = 50.2;
    private PhysicalAttributes physicalAttributes = new PhysicalAttributes(gender, weight);
    private Amount targetAmount = new Amount(1_000);

    public MemberFixture memberNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
        return this;
    }

    public MemberFixture gender(Gender gender) {
        this.gender = gender;
        this.physicalAttributes = physicalAttributes(this.gender, this.weight);
        return this;
    }

    public MemberFixture weight(Double weight) {
        this.weight = weight;
        this.physicalAttributes = physicalAttributes(this.gender, this.weight);
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

    private PhysicalAttributes physicalAttributes(Gender gender, Double weight) {
        return new PhysicalAttributes(gender, weight);
    }
}
