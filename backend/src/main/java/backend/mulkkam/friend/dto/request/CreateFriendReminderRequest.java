package backend.mulkkam.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "물풍선 보내기 요청")
public record CreateFriendReminderRequest(
        @Schema(description = "물풍선(친구 리마인더 알림)을 보낼 친구의 멤버 id", example = "1")
        @NotNull Long memberId
) {
}
