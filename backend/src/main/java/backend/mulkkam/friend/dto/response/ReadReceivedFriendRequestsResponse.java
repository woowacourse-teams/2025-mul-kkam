package backend.mulkkam.friend.dto.response;

import java.util.List;

public record ReadReceivedFriendRequestsResponse(
        List<FriendRequestResponse> friendRequestResponses,
        Long nextId,
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
