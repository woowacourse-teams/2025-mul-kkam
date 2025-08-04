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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class IntakeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "intakeAmount", nullable = false))
    private Amount intakeAmount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "targetAmount", nullable = false))
    private Amount targetAmount;

    public IntakeHistory(
            Member member,
            LocalDateTime dateTime,
            Amount intakeAmount,
            Amount targetAmount
    ) {
        this.member = member;
        this.dateTime = dateTime;
        this.intakeAmount = intakeAmount;
        this.targetAmount = targetAmount;
    }

    public boolean isOwnedBy(Member comparedMember) {
        return this.member.equals(comparedMember);
    }
}
