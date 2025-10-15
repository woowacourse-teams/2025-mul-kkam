package backend.mulkkam.member.dto.response;

import backend.mulkkam.friend.domain.FriendStatus;
import backend.mulkkam.friend.domain.RequestDirection;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSearchItemResponse(
        @Schema(description = "회원 id", example = "1")
        Long id,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "친구 관계 상태", example = "ACCEPTED")
        FriendStatus friendStatus,

        @Schema(description = "친구 관계 상태가 REQUESTED 일 때, 누가 보냈는 지", example = "REQUESTED_BY_ME")
        RequestDirection requestDirection
) {
}
