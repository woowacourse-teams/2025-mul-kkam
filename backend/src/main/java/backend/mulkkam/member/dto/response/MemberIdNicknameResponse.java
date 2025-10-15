package backend.mulkkam.member.dto.response;

import backend.mulkkam.friend.domain.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberIdNicknameResponse(
        @Schema(description = "회원 id", example = "1")
        Long id,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "친구 관계 상태", example = "ACCEPTED")
        FriendStatus friendStatus
) {
}
