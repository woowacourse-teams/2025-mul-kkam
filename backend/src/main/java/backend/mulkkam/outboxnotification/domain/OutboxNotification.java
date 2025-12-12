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

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}
