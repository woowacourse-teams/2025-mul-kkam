package backend.mulkkam.admin.dto.response;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import java.time.LocalDateTime;

public record GetAdminFriendRelationListResponse(
        Long id,
        Long requesterId,
        Long addresseeId,
        FriendRelationStatus status,
        LocalDateTime createdAt
) {
    public static GetAdminFriendRelationListResponse from(FriendRelation friendRelation) {
        return new GetAdminFriendRelationListResponse(
                friendRelation.getId(),
                friendRelation.getRequesterId(),
                friendRelation.getAddresseeId(),
                friendRelation.getFriendRelationStatus(),
                friendRelation.getCreatedAt()
        );
    }
}
