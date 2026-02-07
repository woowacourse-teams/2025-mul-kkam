package backend.mulkkam.notification.dto.request;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.domain.SuggestionNotification;
import java.time.LocalDateTime;

public record CreateTokenSuggestionNotificationRequest(
        String title,
        String body,
        Member member,
        int recommendedTargetAmount,
        LocalDateTime createdAt
) {

    public Notification toNotification() {
        return new Notification(
                NotificationType.SUGGESTION,
                body,
                false,
                createdAt,
                member
        );
    }

    public SuggestionNotification toSuggestionNotification(Notification notification) {
        return new SuggestionNotification(
                recommendedTargetAmount,
                false,
                notification
        );
    }

    public SendMessageByFcmTokenRequest toSendMessageByFcmTokenRequest(String token) {
        return new SendMessageByFcmTokenRequest(
                title,
                body,
                token,
                DevicePlatform.ANDROID,
                Action.GO_NOTIFICATION
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
                Action.GO_NOTIFICATION
        );
    }
}
