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
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
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
    @AttributeOverride(name = "value", column = @Column(name = "target_amount", nullable = false))
    private Amount targetAmount;

    @Column(nullable = false)
    private boolean isMarketingNotificationAgreed;

    @Column(nullable = false)
    private boolean isNightNotificationAgreed;

    public Member(
            MemberNickname memberNickname,
            PhysicalAttributes physicalAttributes,
            Amount targetAmount,
            boolean isMarketingNotificationAgreed,
            boolean isNightNotificationAgreed
    ) {
        this.memberNickname = memberNickname;
        this.physicalAttributes = physicalAttributes;
        this.targetAmount = targetAmount;
        this.isMarketingNotificationAgreed = isMarketingNotificationAgreed;
        this.isNightNotificationAgreed = isNightNotificationAgreed;
    }

    public PhysicalAttributes getPhysicalAttributes() {
        if (physicalAttributes == null) {
            return new PhysicalAttributes(null, null);
        }
        return physicalAttributes;
    }

    public void updateNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
    }

    public void updatePhysicalAttributes(PhysicalAttributes physicalAttributes) {
        this.physicalAttributes = physicalAttributes;
    }

    public void updateTargetAmount(Amount newTargetAmount) {
        this.targetAmount = newTargetAmount;
    }

    public boolean isSameNickname(MemberNickname memberNickname) {
        return this.memberNickname.equals(memberNickname);
    }

    public void modifyIsNightNotificationAgreed(boolean isNightNotificationAgreed) {
        this.isNightNotificationAgreed = isNightNotificationAgreed;
    }

    public void modifyIsMarketingNotificationAgreed(boolean isMarketingNotificationAgreed) {
        this.isMarketingNotificationAgreed = isMarketingNotificationAgreed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Member other)) {
            return false;
        }
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
