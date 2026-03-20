package backend.mulkkam.averageTemperature.dto;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;
import java.util.List;

public record CreateTokenNotificationRequest(
        String title,
        String body,
        Member member,
        Action action,
        NotificationType notificationType,
        LocalDateTime createdAt
) {

    public SendMessageByFcmTokenRequest toSendMessageByFcmTokenRequest(String token) {
        return new SendMessageByFcmTokenRequest(
                title,
                body,
                token,
                DevicePlatform.ANDROID,
                action
        );
    }

    public SendMessageByFcmTokensRequest toSendMessageByFcmTokensRequest(List<String> tokens) {
        return new SendMessageByFcmTokensRequest(
                title,
                body,
                tokens,
                DevicePlatform.ANDROID,
                action
        );
    }

    public SendMessageByFcmTokenRequest toSendMessageByFcmTokenRequest(
            String token,
            DevicePlatform platform
    ) {
        return new SendMessageByFcmTokenRequest(
                title,
                body,
                token,
                platform,
                action
        );
    }

    public SendMessageByFcmTokensRequest toSendMessageByFcmTokensRequest(
            List<String> tokens,
            DevicePlatform platform
    ) {
        return new SendMessageByFcmTokensRequest(
                title,
                body,
                tokens,
                platform,
                action
        );
    }

    public Notification toNotification() {
        return new Notification(
                notificationType,
                body,
                createdAt,
                member
        );
    }
}
