package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReadReceivedFriendRelationResponse(
        @Schema(description = "친구 신청 엔티티 id 및 닉네임")
        List<FriendRelationResponse> friendRelationResponses,

        @Schema(description = "다음 요청 시 보내야할 id", example = "25")
        Long nextId,

        @Schema(description = "다음 데이터가 있는 지 체크", example = "true")
        boolean hasNext
) {
    public static ReadReceivedFriendRelationResponse of(List<FriendRelationResponse> friendRelationResponses,
                                                        int size) {
        boolean hasNext = friendRelationResponses.size() > size;
        Long nextId = null;

        if (hasNext) {
            nextId = friendRelationResponses.get(size - 1).friendRequestId();
            friendRelationResponses = friendRelationResponses.subList(0, size);
        } else if (!friendRelationResponses.isEmpty()) {
            nextId = friendRelationResponses.getLast().friendRequestId();
        }

        return new ReadReceivedFriendRelationResponse(friendRelationResponses, nextId, hasNext);
    }
}
