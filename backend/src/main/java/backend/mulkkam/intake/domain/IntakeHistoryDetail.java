package backend.mulkkam.intake.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.member.domain.Member;
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
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE intake_history_detail SET deleted_at = NOW() WHERE id = ?")
@Entity
public class IntakeHistoryDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime intakeTime;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "intake_amount", nullable = false))
    private IntakeAmount intakeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private IntakeHistory intakeHistory;

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            IntakeAmount intakeAmount,
            IntakeHistory intakeHistory
    ) {
        this.intakeTime = intakeTime;
        this.intakeAmount = intakeAmount;
        this.intakeHistory = intakeHistory;
    }

    public boolean isOwnedBy(Member comparedMember) {
        return this.intakeHistory.isOwnedBy(comparedMember);
    }

    public boolean isCreatedAt(LocalDate comparedDate) {
        return this.intakeHistory.isCreatedAt(comparedDate);
    }
}
