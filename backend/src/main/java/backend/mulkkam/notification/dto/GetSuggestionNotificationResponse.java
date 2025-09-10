package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.domain.SuggestionNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "개별 제안 알림 응답")
public record GetSuggestionNotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 내용", example = "추천 음용량은 다음과 같아요 적용하실래요?")
        String content,

        @Schema(description = "알림 타입", example = "SUGGESTION", implementation = NotificationType.class)
        String type,

        @Schema(description = "알림 생성 시간", example = "2024-01-15T09:30:00")
        LocalDateTime createdAt,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "추천 목표 음용량", example = "2000")
        int recommendedTargetAmount,

        @Schema(description = "추천 음용량 적용 유무", example = "false")
        boolean applyRecommendAmount
) implements NotificationResponse {

    public GetSuggestionNotificationResponse(Notification notification, SuggestionNotification suggestionNotification) {
        this(
                notification.getId(),
                notification.getContent(),
                notification.getNotificationType().name(),
                notification.getCreatedAt(),
                notification.isRead(),
                suggestionNotification.getRecommendedTargetAmount(),
                suggestionNotification.isApplyTargetAmount()
        );
    }
}
