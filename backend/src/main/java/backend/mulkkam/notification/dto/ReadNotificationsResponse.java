package backend.mulkkam.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "알림 목록 조회 응답")
public record ReadNotificationsResponse(
        @Schema(description = "알림 목록")
        List<ReadNotificationResponse> readNotificationResponses,

        @Schema(description = "다음 페이지 조회를 위한 커서 ID (다음 페이지가 없으면 null)", example = "80")
        Long nextCursor
) {
}
