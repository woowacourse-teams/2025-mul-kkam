package backend.mulkkam.notification.dto;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "알림 목록 조회 응답")
    public static record ReadNotificationsResponse(
            @Schema(description = "알림 목록")
            List<NotificationResponse> readNotificationResponses,

            @Schema(description = "다음 페이지 조회를 위한 커서 ID (다음 페이지가 없으면 null)", example = "80")
            Long nextCursor
    ) {
    }
}
