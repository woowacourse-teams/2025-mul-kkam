package backend.mulkkam.notification.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;

import java.time.LocalDateTime;

public record CreateTopicNotificationRequest(
        String title,
        String body,
        String topic,
        Action action,
        NotificationType notificationType,
        LocalDateTime createdAt,
        TargetAmount recommendedTargetAmount
) {
    public SendMessageByFcmTopicRequest toSendMessageByFcmTopicRequest() {
        return new SendMessageByFcmTopicRequest(
                title,
                body,
                topic,
                action
        );
    }

    public Notification toNotification(Member member) {
        return new Notification(
                notificationType,
                body,
                createdAt,
                recommendedTargetAmount,
                member
        );
    }
}
