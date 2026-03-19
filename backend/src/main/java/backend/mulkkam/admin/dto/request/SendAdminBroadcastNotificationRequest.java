package backend.mulkkam.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "어드민 전체 알림 전송 요청")
public record SendAdminBroadcastNotificationRequest(
        @Schema(description = "알림 제목", example = "서비스 점검 안내")
        @NotBlank(message = "알림 제목은 필수입니다.")
        @Size(max = 100, message = "알림 제목은 100자 이하여야 합니다.")
        String title,

        @Schema(description = "알림 내용", example = "2026년 1월 6일 02:00 ~ 06:00 서비스 점검이 예정되어 있습니다.")
        @NotBlank(message = "알림 내용은 필수입니다.")
        @Size(max = 500, message = "알림 내용은 500자 이하여야 합니다.")
        String body
) {
}
