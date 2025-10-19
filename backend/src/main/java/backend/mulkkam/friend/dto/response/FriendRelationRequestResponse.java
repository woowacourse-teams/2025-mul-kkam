package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record FriendRelationRequestResponse(
        @Schema(description = "친구 신청 엔티티 id", example = "1")
        Long friendRequestId,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "친구 신청 생성일", example = "2025-10-15T09:30:00")
        LocalDateTime createdAt
) {
}
