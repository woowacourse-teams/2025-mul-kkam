package backend.mulkkam.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "알림 목록 조회 요청")
public record GetNotificationsRequest(
        @Schema(description = "마지막 조회한 알림 ID (첫 조회시 null)", example = "100")
        Long lastId,

        @Schema(description = "클라이언트 현재 시간 (최근 ??일 이내 알림만 조회 가능)", example = "2024-01-15T10:00:00")
        @NotNull LocalDateTime clientTime,

        @Schema(description = "조회할 알림 개수", example = "20")
        int size
) {
}
