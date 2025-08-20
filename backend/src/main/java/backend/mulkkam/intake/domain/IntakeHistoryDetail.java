package backend.mulkkam.intake.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private IntakeHistory intakeHistory;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private IntakeType intakeType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "intake_amount", nullable = false))
    private IntakeAmount intakeAmount;

    private String cupEmojiUrl;

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            IntakeHistory intakeHistory,
            IntakeType intakeType,
            Cup cup
    ) {
        this.intakeTime = intakeTime;
        this.intakeHistory = intakeHistory;
        this.intakeType = intakeType;
        this.intakeAmount = new IntakeAmount(cup.getCupAmount().value());
        this.cupEmojiUrl = cup.getCupEmoji().getUrl();
    }

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            IntakeHistory intakeHistory,
            IntakeType intakeType,
            IntakeAmount intakeAmount
    ) {
        this.intakeTime = intakeTime;
        this.intakeHistory = intakeHistory;
        this.intakeType = intakeType;
        this.intakeAmount = intakeAmount;
    }

    public boolean isOwnedBy(Member comparedMember) {
        return this.intakeHistory.isOwnedBy(comparedMember);
    }

    public boolean isCreatedAt(LocalDate comparedDate) {
        return this.intakeHistory.isCreatedAt(comparedDate);
    }

    public boolean isNonExistingCupEmojiUrl() { // TODO 2025. 8. 20. 17:30: 네이밍 수정
        return cupEmojiUrl == null;
    }
}
