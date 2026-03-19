package backend.mulkkam.common.infrastructure.fcm.domain;

import backend.mulkkam.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class NotificationOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationOutboxTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationOutboxStatus status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    private String topic;

    private String token;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private int maxAttempts;

    @Column(nullable = false)
    private LocalDateTime nextAttemptAt;

    private LocalDateTime sentAt;

    private String lastErrorCode;

    @Lob
    private String lastErrorMessage;

    private NotificationOutbox(
            NotificationOutboxTargetType targetType,
            String title,
            String body,
            Action action,
            String topic,
            String token,
            int maxAttempts,
            LocalDateTime now
    ) {
        super();
        this.targetType = targetType;
        this.status = NotificationOutboxStatus.PENDING;
        this.title = title;
        this.body = body;
        this.action = action;
        this.topic = topic;
        this.token = token;
        this.attemptCount = 0;
        this.maxAttempts = maxAttempts;
        this.nextAttemptAt = now;
    }

    public static NotificationOutbox forTopic(
            String title,
            String body,
            String topic,
            Action action,
            int maxAttempts,
            LocalDateTime now
    ) {
        return new NotificationOutbox(
                NotificationOutboxTargetType.TOPIC,
                title,
                body,
                action,
                topic,
                null,
                maxAttempts,
                now
        );
    }

    public static NotificationOutbox forToken(
            String title,
            String body,
            String token,
            Action action,
            int maxAttempts,
            LocalDateTime now
    ) {
        return new NotificationOutbox(
                NotificationOutboxTargetType.TOKEN,
                title,
                body,
                action,
                null,
                token,
                maxAttempts,
                now
        );
    }

    public void markSuccess(LocalDateTime now) {
        this.attemptCount += 1;
        this.status = NotificationOutboxStatus.SUCCESS;
        this.sentAt = now;
        this.nextAttemptAt = now;
        this.lastErrorCode = null;
        this.lastErrorMessage = null;
    }

    public void markFailure(
            LocalDateTime now,
            String errorCode,
            String errorMessage,
            int baseBackoffSeconds
    ) {
        this.attemptCount += 1;
        this.lastErrorCode = errorCode;
        this.lastErrorMessage = errorMessage;
        if (this.attemptCount >= this.maxAttempts) {
            this.status = NotificationOutboxStatus.FAILED;
            this.nextAttemptAt = now;
            return;
        }
        this.status = NotificationOutboxStatus.PENDING;
        long multiplier = 1L << (this.attemptCount - 1);
        this.nextAttemptAt = now.plusSeconds(baseBackoffSeconds * multiplier);
    }

    public void markFailedNow(
            LocalDateTime now,
            String errorCode,
            String errorMessage
    ) {
        this.attemptCount += 1;
        this.status = NotificationOutboxStatus.FAILED;
        this.lastErrorCode = errorCode;
        this.lastErrorMessage = errorMessage;
        this.nextAttemptAt = now;
    }
}
