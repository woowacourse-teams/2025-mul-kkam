package backend.mulkkam.notification.domain;

import backend.mulkkam.member.domain.vo.TargetAmount;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "recommended_target_amount"))
    private TargetAmount recommendedTargetAmount;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Notification(
            NotificationType notificationType,
            String title,
            LocalDateTime createdAt,
            TargetAmount recommendedTargetAmount,
            Member member
    ) {
        this.notificationType = notificationType;
        this.title = title;
        this.isRead = false;
        this.createdAt = createdAt;
        this.recommendedTargetAmount = recommendedTargetAmount;
        this.member = member;
    }

    public Notification(
            NotificationType notificationType,
            String title,
            boolean isRead,
            LocalDateTime createdAt,
            TargetAmount recommendedTargetAmount,
            Member member
    ) {
        this.notificationType = notificationType;
        this.title = title;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.recommendedTargetAmount = recommendedTargetAmount;
        this.member = member;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
