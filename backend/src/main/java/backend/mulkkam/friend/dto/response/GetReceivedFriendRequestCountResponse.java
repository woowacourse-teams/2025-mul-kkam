package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetReceivedFriendRequestCountResponse(
        @Schema(description = "내가 받은 친구 신청 갯수", example = "22")
        Long count
) {
}
