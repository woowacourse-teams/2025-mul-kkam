package backend.mulkkam.intake.domain;

import backend.mulkkam.intake.domain.vo.Amount;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@Entity
public class IntakeHistoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime intakeTime;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "intake_amount", nullable = false))
    private Amount intakeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private IntakeHistory intakeHistory;

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            Amount intakeAmount,
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
