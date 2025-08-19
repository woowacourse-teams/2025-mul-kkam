package backend.mulkkam.member.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "nickname", nullable = false, length = MemberNickname.MAX_LENGTH)
    )
    private MemberNickname memberNickname;

    @Embedded
    private PhysicalAttributes physicalAttributes;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "target_amount", nullable = false))
    private TargetAmount targetAmount;

    @Column(nullable = false)
    private boolean isMarketingNotificationAgreed;

    @Column(nullable = false)
    private boolean isNightNotificationAgreed;

    @Column(nullable = true, unique = true)
    private String activeNickname;

    public Member(
            MemberNickname memberNickname,
            PhysicalAttributes physicalAttributes,
            TargetAmount targetAmount,
            boolean isMarketingNotificationAgreed,
            boolean isNightNotificationAgreed,
            String activeNickname
    ) {
        this.memberNickname = memberNickname;
        this.physicalAttributes = physicalAttributes;
        this.targetAmount = targetAmount;
        this.isMarketingNotificationAgreed = isMarketingNotificationAgreed;
        this.isNightNotificationAgreed = isNightNotificationAgreed;
        this.activeNickname = activeNickname;
    }

    public PhysicalAttributes getPhysicalAttributes() {
        if (physicalAttributes == null) {
            return new PhysicalAttributes(null, null);
        }
        return physicalAttributes;
    }

    public void updateNickname(MemberNickname memberNickname) {
        this.memberNickname = memberNickname;
        this.activeNickname = memberNickname.value();
    }

    public void updatePhysicalAttributes(PhysicalAttributes physicalAttributes) {
        this.physicalAttributes = physicalAttributes;
    }

    public void updateTargetAmount(TargetAmount newTargetAmount) {
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

    public void updateActiveNickname(String activeNickname) {
        this.activeNickname = activeNickname;
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
