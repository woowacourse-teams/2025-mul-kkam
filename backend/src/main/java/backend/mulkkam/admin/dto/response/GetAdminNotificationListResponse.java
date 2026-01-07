package backend.mulkkam.admin.dto.response;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record GetAdminNotificationListResponse(
        Long id,
        Long memberId,
        String memberNickname,
        NotificationType notificationType,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static GetAdminNotificationListResponse from(Notification notification) {
        return new GetAdminNotificationListResponse(
                notification.getId(),
                notification.getMember().getId(),
                notification.getMember().getMemberNickname() != null ? notification.getMember().getMemberNickname().value() : null,
                notification.getNotificationType(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
