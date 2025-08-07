package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.Notification;
import java.time.LocalDateTime;

public record ReadNotificationResponse(
        Long id,
        String title,
        String type,
        LocalDateTime createdAt,
        int recommendedTargetAmount,
        boolean isRead
) {

    public ReadNotificationResponse(Notification notification) {
        this(
                notification.getId(),
                notification.getTitle(),
                notification.getNotificationType().name(),
                notification.getCreatedAt(),
                notification.getRecommendedTargetAmount().value(),
                notification.isRead()
        );
    }
}
