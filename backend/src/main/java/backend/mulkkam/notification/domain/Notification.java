package backend.mulkkam.notification.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE notification SET deleted_at = NOW() WHERE id = ?")
@Entity
public class Notification extends BaseEntity {

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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "recommended_target_amount"))
    private TargetAmount recommendedTargetAmount;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Notification(
            NotificationType notificationType,
            String title,
            LocalDateTime createdAt, // TODO: 필드 초기화 시점 고려해서 처리할 것
            TargetAmount recommendedTargetAmount,
            Member member
    ) {
        this.notificationType = notificationType;
        this.title = title;
        this.createdAt = createdAt;
        this.isRead = false;
        this.recommendedTargetAmount = recommendedTargetAmount;
        this.member = member;
    }

    public Notification(
            NotificationType notificationType,
            String title,
            boolean isRead,
            LocalDateTime createdAt, // TODO: 필드 초기화 시점 고려해서 처리할 것
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
