package backend.mulkkam.notification.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.Member;
import jakarta.persistence.Column;
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
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Notification(
            NotificationType notificationType,
            String content,
            LocalDateTime createdAt, // TODO: 필드 초기화 시점 고려해서 처리할 것
            Member member
    ) {
        this.notificationType = notificationType;
        this.content = content;
        this.isRead = false;
        this.createdAt = createdAt;
        this.member = member;
    }

    public Notification(
            NotificationType notificationType,
            String content,
            boolean isRead,
            LocalDateTime createdAt, // TODO: 필드 초기화 시점 고려해서 처리할 것
            Member member
    ) {
        this.notificationType = notificationType;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.member = member;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
