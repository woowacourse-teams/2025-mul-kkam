package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FCM 토큰 기반 메시지 전송 요청")
public record SendMessageByFcmTokenRequest(
        @Schema(description = "알림 제목", example = "밍곰님, 목표 달성까지 500ml 남았어요!")
        String title,

        @Schema(description = "알림 내용", example = "오늘의 목표를 달성하기 위해 물 두 잔만 더 마셔보세요")
        String body,

        @Schema(description = "FCM 디바이스 토큰", example = "dKz9...FCM토큰...X2c")
        String token,

        @Schema(description = "클릭 시 실행할 액션", example = "GO_HOME", implementation = Action.class)
        Action action
) {
}
