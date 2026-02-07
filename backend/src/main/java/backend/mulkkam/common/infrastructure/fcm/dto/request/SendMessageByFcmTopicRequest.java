package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FCM 토픽 기반 메시지 전송 요청")
public record SendMessageByFcmTopicRequest(
        @Schema(description = "알림 제목", example = "💧 물 마실 시간입니다!")
        String title,

        @Schema(description = "알림 내용", example = "건강한 하루를 위해 지금 한 잔의 물을 마셔보세요")
        String body,

        @Schema(description = "FCM 토픽명", example = "REMIND", implementation = NotificationType.class)
        String topic,

        @Schema(description = "디바이스 플랫폼", example = "ANDROID")
        DevicePlatform platform,

        @Schema(description = "클릭 시 실행할 액션", example = "GO_HOME", implementation = Action.class)
        Action action
) {
    public SendMessageByFcmTopicRequest(
            String title,
            String body,
            String topic,
            Action action
    ) {
        this(title, body, topic, DevicePlatform.ANDROID, action);
    }
}
