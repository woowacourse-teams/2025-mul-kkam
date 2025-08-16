package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "개별 알림 응답")
public record ReadNotificationResponse(
        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 제목", example = "물 마실 시간이에요!")
        String title,

        @Schema(description = "알림 타입", example = "REMINDER", implementation = NotificationType.class)
        String type,

        @Schema(description = "알림 생성 시간", example = "2024-01-15T09:30:00")
        LocalDateTime createdAt,

        @Schema(description = "권장 목표량 (ml)", example = "2000", minimum = "0")
        int recommendedTargetAmount,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead
) {

    public ReadNotificationResponse(Notification notification) {
        this(
                notification.getId(),
                notification.getContent(),
                notification.getNotificationType().name(),
                notification.getCreatedAt(),
                1000, // TODO 2025. 8. 16. 17:19: 수정
                notification.isRead()
        );
    }
}
