package backend.mulkkam.intake.domain;

import backend.mulkkam.member.domain.vo.TargetAmount;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TargetAmountSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate updatedAt;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "target_amount", nullable = false))
    private TargetAmount targetAmount;

    public TargetAmountSnapshot(
            Member member,
            LocalDate updatedAt,
            TargetAmount targetAmount
    ) {
        this.member = member;
        this.updatedAt = updatedAt;
        this.targetAmount = targetAmount;
    }

    public void updateTargetAmount(TargetAmount targetAmount) {
        this.targetAmount = targetAmount;
    }
}
