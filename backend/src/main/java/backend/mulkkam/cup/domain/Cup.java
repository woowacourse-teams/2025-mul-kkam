package backend.mulkkam.cup.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE cup SET deleted_at = NOW() WHERE id = ?")
@Entity
public class Cup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "nickname", nullable = false, length = CupNickname.MAX_LENGTH)
    )
    private CupNickname nickname;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "cup_amount", nullable = false)
    )
    private CupAmount cupAmount;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "cup_rank", nullable = false)
    )
    private CupRank cupRank;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private IntakeType intakeType;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CupEmoji cupEmoji;

    public Cup(Member member,
               CupNickname nickname,
               CupAmount cupAmount,
               CupRank cupRank,
               IntakeType intakeType,
               CupEmoji cupEmoji
    ) {
        this.member = member;
        this.nickname = nickname;
        this.cupAmount = cupAmount;
        this.cupRank = cupRank;
        this.intakeType = intakeType;
        this.cupEmoji = cupEmoji;
    }

    public void update(
            CupNickname nickname,
            CupAmount cupAmount,
            IntakeType intakeType,
            CupEmoji cupEmoji
    ) {
        this.nickname = nickname;
        this.cupAmount = cupAmount;
        this.intakeType = intakeType;
        this.cupEmoji = cupEmoji;
    }

    public int calculateHydration() {
        return intakeType.calculateHydration(cupAmount.value());
    }

    public boolean isLowerPriorityThan(Cup other) {
        return cupRank.hasLowerPriorityThan(other.getCupRank());
    }

    public void promoteRank() {
        cupRank = cupRank.promote();
    }

    public boolean isOwnedBy(Member member) {
        Long ownerId = this.member.getId();
        Long otherId = member.getId();
        return ownerId.equals(otherId);
    }

    public void modifyRank(CupRank rank) {
        this.cupRank = rank;
    }
}
