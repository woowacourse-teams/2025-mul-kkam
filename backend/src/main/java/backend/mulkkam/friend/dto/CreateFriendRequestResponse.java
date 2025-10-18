package backend.mulkkam.friend.dto;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;

public record CreateFriendRequestResponse(
        Long id,
        FriendRelationStatus status
) {
    public CreateFriendRequestResponse(FriendRelation friendRelation) {
        this(friendRelation.getId(), friendRelation.getFriendRelationStatus());
    }
}
