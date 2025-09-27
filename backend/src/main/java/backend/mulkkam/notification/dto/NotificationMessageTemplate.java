package backend.mulkkam.notification.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationMessageTemplate(
        String title,
        String body,
        String topic,
        Action action,
        NotificationType type
) {
    public Notification toNotification(Member member, LocalDateTime createdAt) {
        return new Notification(
                type,
                body,
                createdAt,
                member
        );
    }

    public SendMessageByFcmTopicRequest toSendMessageByFcmTopicRequest() {
        return new SendMessageByFcmTopicRequest(
                title,
                body,
                topic,
                action
        );
    }
}
