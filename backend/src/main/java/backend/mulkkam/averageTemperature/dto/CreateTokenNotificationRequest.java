package backend.mulkkam.averageTemperature.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record CreateTokenNotificationRequest(
        String title,
        String body,
        Member member,
        Action action,
        NotificationType notificationType,
        Amount recommendedTargetAmount,
        LocalDateTime createdAt
) {

    public SendMessageByFcmTokenRequest toFcmToken(String token) {
        return new SendMessageByFcmTokenRequest(
                title,
                body,
                token,
                action
        );
    }

    public Notification toNotification() {
        return new Notification(
                notificationType,
                body,
                createdAt,
                recommendedTargetAmount,
                member
        );
    }
}
