package backend.mulkkam.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 대시보드 통계 응답")
public record GetAdminDashboardStatsResponse(
        @Schema(description = "총 회원 수", example = "1234")
        long totalMembers,

        @Schema(description = "총 컵 수", example = "567")
        long totalCups,

        @Schema(description = "총 디바이스 수", example = "890")
        long totalDevices,

        @Schema(description = "오늘 섭취 기록 수", example = "123")
        long todayIntakeHistories,

        @Schema(description = "총 알림 수", example = "4567")
        long totalNotifications,

        @Schema(description = "총 친구 관계 수", example = "234")
        long totalFriendRelations
) {
}
