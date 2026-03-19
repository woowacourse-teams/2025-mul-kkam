package backend.mulkkam.admin.dto.request;

import backend.mulkkam.friend.domain.FriendRelationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "어드민 친구 관계 수정 요청")
public record UpdateAdminFriendRelationRequest(
        @Schema(description = "친구 관계 상태", example = "ACCEPTED")
        @NotNull(message = "친구 관계 상태는 필수입니다.")
        FriendRelationStatus status
) {
}
