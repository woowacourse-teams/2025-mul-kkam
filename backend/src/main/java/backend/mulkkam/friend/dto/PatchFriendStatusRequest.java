package backend.mulkkam.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "친구 신청 거절/수락 요청")
public record PatchFriendStatusRequest(
    @Schema(description = "변경하려는 상태", implementation = FriendRequestStatus.class)
    @NotNull FriendRequestStatus status
) {

    public enum FriendRequestStatus {
        ACCEPT,
        REJECT,
        ;
    }
}
