package backend.mulkkam.messaging.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationMessage(
        String messageId,
        Long memberId,
        String token,
        String title,
        String body,
        Action action,
        NotificationType type,
        LocalDateTime createdAt,
        int attemptCount
) implements Serializable {

    public static NotificationMessage create(
            Long memberId,
            String token,
            String title,
            String body,
            Action action,
            NotificationType type
    ) {
        return new NotificationMessage(
                UUID.randomUUID().toString(),
                memberId,
                token,
                title,
                body,
                action,
                type,
                LocalDateTime.now(),
                0
        );
    }

    public NotificationMessage incrementAttempt() {
        return new NotificationMessage(
                messageId,
                memberId,
                token,
                title,
                body,
                action,
                type,
                createdAt,
                attemptCount + 1
        );
    }
}
