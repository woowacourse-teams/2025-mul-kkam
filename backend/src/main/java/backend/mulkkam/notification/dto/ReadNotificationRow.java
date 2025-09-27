package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record ReadNotificationRow(
        Long id,
        LocalDateTime createdAt,
        String content,
        NotificationType notificationType,
        Boolean isRead,
        Integer recommendedTargetAmount,
        Boolean applyTargetAmount
) {
}
