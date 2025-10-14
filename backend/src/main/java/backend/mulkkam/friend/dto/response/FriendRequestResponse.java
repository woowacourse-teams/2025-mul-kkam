package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendRequestResponse(
        @Schema(description = "친구 신청 엔티티 id", example = "1")
        Long friendRequestId,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname
) {
}
