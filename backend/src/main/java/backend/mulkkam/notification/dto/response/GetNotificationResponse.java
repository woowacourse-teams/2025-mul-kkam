package backend.mulkkam.notification.dto.response;

import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.ReadNotificationRow;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "개별 일반 알림 응답")
public record GetNotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 내용", example = "물 마실 시간이에요!")
        String content,

        @Schema(description = "알림 타입", example = "REMIND", implementation = NotificationType.class)
        String type,

        @Schema(description = "알림 생성 시간", example = "2024-01-15T09:30:00")
        LocalDateTime createdAt,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead
) implements NotificationResponse {

    public GetNotificationResponse(ReadNotificationRow readNotificationRow) {
        this(
                readNotificationRow.id(),
                readNotificationRow.content(),
                readNotificationRow.notificationType().name(),
                readNotificationRow.createdAt(),
                readNotificationRow.isRead()
        );
    }
}
