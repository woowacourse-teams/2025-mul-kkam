package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class FriendRequestService {

    private final FriendService friendService;

    @Transactional
    public void modifyFriendStatus(
            Long friendRelationId,
            PatchFriendStatusRequest request,
            MemberDetails memberDetails
    ) {
        switch (request.status()) {
            case ACCEPT -> acceptFriend(friendRelationId, memberDetails);
            case REJECT -> rejectFriendRequest(friendRelationId, memberDetails);
        }
    }

    private void acceptFriend(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        FriendRelation friendRelation = friendService.getFriendRelation(friendRelationId);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendRelation.updateAccepted();
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);

    }

    private void rejectFriendRequest(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        FriendRelation friendRelation = friendService.getFriendRelation(friendRelationId);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendService.delete(friendRelationId, memberDetails);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }
}
