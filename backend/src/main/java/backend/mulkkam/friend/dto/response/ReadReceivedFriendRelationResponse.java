package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReadReceivedFriendRelationResponse(
        @Schema(description = "친구 신청 엔티티 id 및 닉네임")
        List<FriendRelationRequestResponse> friendRelationResponses,

        @Schema(description = "다음 요청 시 보내야할 id", example = "25")
        Long nextId,

        @Schema(description = "다음 데이터가 있는 지 체크", example = "true")
        boolean hasNext
) {
    public static ReadReceivedFriendRelationResponse of(
            List<FriendRelationRequestResponse> friendRelationRequestResponses,
            int size) {
        boolean hasNext = friendRelationRequestResponses.size() > size;
        Long nextId = null;

        if (hasNext) {
            nextId = friendRelationRequestResponses.get(size - 1).friendRequestId();
            friendRelationRequestResponses = friendRelationRequestResponses.subList(0, size);
        } else if (!friendRelationRequestResponses.isEmpty()) {
            nextId = friendRelationRequestResponses.getLast().friendRequestId();
        }

        return new ReadReceivedFriendRelationResponse(friendRelationRequestResponses, nextId, hasNext);
    }
}
