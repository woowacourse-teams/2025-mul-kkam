package backend.mulkkam.member.domain;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.PhysicalAttributes;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "nickname", nullable = false, unique = true, length = MemberNickname.MAX_LENGTH)
    )
    private MemberNickname memberNickname;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    private Integer weight;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "targetAmount", nullable = false))
    private Amount targetAmount;

    public Member(
            MemberNickname memberNickname,
            Gender gender,
            Integer weight,
            Amount targetAmount
    ) {
        this.memberNickname = memberNickname;
        this.gender = gender;
        this.weight = weight;
        this.targetAmount = targetAmount;
    }

    public void setPhysicalAttributes(PhysicalAttributes physicalAttributes) {
        if (physicalAttributes.getGender() != null) {
            this.gender = physicalAttributes.getGender();
        }
        if (physicalAttributes.getWeight() != null) {
            this.weight = physicalAttributes.getWeight();
        }
    }
}
