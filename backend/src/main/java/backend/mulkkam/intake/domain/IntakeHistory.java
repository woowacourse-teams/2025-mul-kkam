package backend.mulkkam.intake.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE intake_history SET deleted_at = NOW() WHERE id = ?")
@Entity
public class IntakeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate historyDate;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "target_amount", nullable = false))
    private TargetAmount targetAmount;

    @Column(nullable = false)
    private int streak;

    public IntakeHistory(
            Member member,
            LocalDate historyDate,
            TargetAmount targetAmount,
            int streak
    ) {
        this.member = member;
        this.historyDate = historyDate;
        this.targetAmount = targetAmount;
        this.streak = streak;
    }

    public boolean isOwnedBy(Member comparedMember) {
        return this.member.equals(comparedMember);
    }

    public boolean isCreatedAt(LocalDate comparedDate) {
        return this.historyDate.equals(comparedDate);
    }

    public void modifyTargetAmount(TargetAmount targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void addTargetAmount(int targetAmount) {
        this.targetAmount = new TargetAmount(this.targetAmount.value() + targetAmount);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IntakeHistory that = (IntakeHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
