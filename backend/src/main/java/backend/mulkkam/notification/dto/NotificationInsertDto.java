package backend.mulkkam.notification.dto;

import backend.mulkkam.notification.domain.NotificationType;

public record NotificationInsertDto(
        NotificationType notificationType,
        String content,
        Long memberId
) {

    public NotificationInsertDto(
            NotificationMessageTemplate notificationMessageTemplate,
            Long memberId
    ) {
        this(
                notificationMessageTemplate.type(),
                notificationMessageTemplate.body(),
                memberId
        );
    }
}
