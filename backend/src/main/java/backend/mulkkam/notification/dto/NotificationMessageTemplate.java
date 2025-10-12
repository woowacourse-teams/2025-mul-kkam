package backend.mulkkam.notification.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationMessageTemplate(
        String title,
        String body,
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

    public List<Notification> toNotifications(List<Member> members, LocalDateTime createdAt) {
        return members.stream()
                .map(member -> toNotification(member, createdAt))
                .toList();
    }

    public SendMessageByFcmTopicRequest toSendMessageByFcmTopicRequest(String topic) {
        return new SendMessageByFcmTopicRequest(
                title,
                body,
                topic,
                action
        );
    }

    public SendMessageByFcmTokensRequest toSendMessageByFcmTokensRequest(List<String> tokens) {
        return new SendMessageByFcmTokensRequest(
                title,
                body,
                tokens,
                action
        );
    }
}
