package backend.mulkkam.outboxnotification.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@SQLDelete(sql = "UPDATE outbox_notification SET deleted_at = NOW() WHERE id = ?")
@Entity
public class OutboxNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private int attemptCount;

    private String lastError;

    private LocalDateTime nextAttemptAt;

    public enum Status {
        READY, SENDING, SENT, RETRY, FAIL
    }

    public OutboxNotification(
            Long memberId,
            String token,
            String title,
            String body,
            Action action,
            NotificationType type,
            Status status,
            String idempotencyKey,
            int attemptCount,
            String lastError,
            LocalDateTime nextAttemptAt
    ) {
        this.memberId = memberId;
        this.token = token;
        this.title = title;
        this.body = body;
        this.action = action;
        this.type = type;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.attemptCount = attemptCount;
        this.lastError = lastError;
        this.nextAttemptAt = nextAttemptAt;
    }

    public void markSending() {
        this.status = Status.SENDING;
    }

    public void markSent() {
        this.status = Status.SENT;
    }

    public void markRetryOrFail(int maxRetryCount) {
        if (attemptCount + 1 == maxRetryCount) {
            this.status = Status.FAIL;
            return;
        }
        this.status = Status.RETRY;
        this.attemptCount += 1;
        this.nextAttemptAt = nextBackoffTime(this.attemptCount);
    }

    public void markFail() {
        this.status = Status.FAIL;
    }

    public void updateLastError(String errorName) {
        this.lastError = errorName;
    }

    private LocalDateTime nextBackoffTime(int attempt) {
        long sec = Math.min(60, (long) (2 * Math.pow(2, attempt)));
        return LocalDateTime.now().plusSeconds(sec);
    }
}
