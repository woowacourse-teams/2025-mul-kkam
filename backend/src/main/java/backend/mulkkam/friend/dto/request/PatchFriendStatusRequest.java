package backend.mulkkam.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "친구 신청 거절/수락 요청")
public record PatchFriendStatusRequest(

    @Schema(description = "요청자의 멤버 id", example = "1")
    Long memberId,

    @Schema(description = "변경하려는 상태", implementation = Status.class)
    @NotNull Status status
) {

    public enum Status {
        ACCEPTED,
        REJECTED,
        ;
    }
}
