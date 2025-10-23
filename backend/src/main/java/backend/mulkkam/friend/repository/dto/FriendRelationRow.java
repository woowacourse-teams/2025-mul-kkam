package backend.mulkkam.friend.repository.dto;

import backend.mulkkam.friend.domain.FriendRelationStatus;

public record FriendRelationRow(
        Long requesterId,
        Long addresseeId,
        FriendRelationStatus status
) {
}
