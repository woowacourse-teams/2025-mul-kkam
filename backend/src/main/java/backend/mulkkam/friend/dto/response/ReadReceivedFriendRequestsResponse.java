package backend.mulkkam.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReadReceivedFriendRequestsResponse(
        @Schema(description = "친구 신청 엔티티 id 및 닉네임")
        List<FriendRequestResponse> friendRequestResponses,
        
        @Schema(description = "다음 요청 시 보내야할 id", example = "25")
        Long nextId,

        @Schema(description = "다음 데이터가 있는 지 체크", example = "true")
        boolean hasNext
) {
    public static ReadReceivedFriendRequestsResponse of(List<FriendRequestResponse> friendRequestResponses, int size) {
        boolean hasNext = friendRequestResponses.size() > size;
        Long nextId = null;

        if (hasNext) {
            nextId = friendRequestResponses.get(size - 1).friendRequestId();
            friendRequestResponses = friendRequestResponses.subList(0, size);
        } else if (!friendRequestResponses.isEmpty()) {
            nextId = friendRequestResponses.getLast().friendRequestId();
        }

        return new ReadReceivedFriendRequestsResponse(friendRequestResponses, nextId, hasNext);
    }
}
