package backend.mulkkam.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 신청 요청")
public record CreateFriendRequestRequest(
        @Schema(description = "친구를 신청하고 싶은 멤버의 id", example = "1")
        Long memberId
) {
}
