package backend.mulkkam.admin.dto.response;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import java.time.LocalDateTime;

public record AdminFriendRelationListResponse(
        Long id,
        Long requesterId,
        Long addresseeId,
        FriendRelationStatus status,
        LocalDateTime createdAt
) {
    public static AdminFriendRelationListResponse from(FriendRelation friendRelation) {
        return new AdminFriendRelationListResponse(
                friendRelation.getId(),
                friendRelation.getRequesterId(),
                friendRelation.getAddresseeId(),
                friendRelation.getFriendRelationStatus(),
                friendRelation.getCreatedAt()
        );
    }
}
