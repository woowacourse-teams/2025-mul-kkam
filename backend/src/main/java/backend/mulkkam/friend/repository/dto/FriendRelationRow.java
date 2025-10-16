package backend.mulkkam.friend.repository.dto;

import backend.mulkkam.friend.domain.FriendStatus;

public record FriendRelationRow(
        Long requesterId,
        Long addresseeId,
        FriendStatus status
) {
}
