package backend.mulkkam.member.domain;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    private PhysicalAttributes physicalAttributes;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "targetAmount", nullable = false))
    private Amount targetAmount;

    public Member(
            MemberNickname memberNickname,
            PhysicalAttributes physicalAttributes,
            Amount targetAmount
    ) {
        this.memberNickname = memberNickname;
        this.physicalAttributes = physicalAttributes;
        this.targetAmount = targetAmount;
    }

    public void updatePhysicalAttributes(PhysicalAttributes physicalAttributes) {
        this.physicalAttributes = physicalAttributes;
    }
}
