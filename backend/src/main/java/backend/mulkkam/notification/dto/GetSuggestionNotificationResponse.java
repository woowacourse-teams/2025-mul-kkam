package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import java.time.LocalDateTime;

// TODO 2025. 8. 16. 17:44: 스웨거 설정 붙이기
public record GetSuggestionNotificationResponse(
        Long id,

        String content,

        String type,

        LocalDateTime createdAt,

        boolean isRead,

        int recommendedTargetAmount,

        boolean applyRecommendAmount
) implements NotificationResponse {

    public GetSuggestionNotificationResponse(Notification notification, SuggestionNotification suggestionNotification) {
        this(
                notification.getId(),
                notification.getContent(),
                notification.getNotificationType().name(),
                notification.getCreatedAt(),
                notification.isRead(),
                suggestionNotification.getRecommendedTargetAmount().value(),
                suggestionNotification.isApplyTargetAmount()
        );
    }
}
