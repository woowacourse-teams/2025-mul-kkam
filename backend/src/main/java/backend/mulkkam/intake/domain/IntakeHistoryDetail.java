package backend.mulkkam.intake.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "cup_emoji_url", nullable = false))
    private CupEmojiUrl cupEmojiUrl;

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            IntakeHistory intakeHistory,
            Cup cup
    ) {
        this.intakeTime = intakeTime;
        this.intakeHistory = intakeHistory;
        this.intakeType = cup.getIntakeType();
        this.intakeAmount = new IntakeAmount(cup.calculateHydration());
        this.cupEmojiUrl = cup.getCupEmoji().getUrl();
    }

    public IntakeHistoryDetail(
            LocalTime intakeTime,
            IntakeHistory intakeHistory,
            IntakeType intakeType,
            int intakeAmount
    ) {
        this.intakeTime = intakeTime;
        this.intakeHistory = intakeHistory;
        this.intakeType = intakeType;
        this.intakeAmount = new IntakeAmount(intakeType.calculateHydration(intakeAmount));
        this.cupEmojiUrl = CupEmojiUrl.getDefault();
    }

    public boolean isOwnedBy(Member comparedMember) {
        return this.intakeHistory.isOwnedBy(comparedMember);
    }

    public boolean isCreatedAt(LocalDate comparedDate) {
        return this.intakeHistory.isCreatedAt(comparedDate);
    }
}
