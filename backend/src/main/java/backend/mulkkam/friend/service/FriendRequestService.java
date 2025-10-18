package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.ALREADY_ACCEPTED;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.CreateFriendRequestResponse;
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class FriendRequestService {

    private final FriendQueryService friendQueryService;
    private final FriendCommandService friendCommandService;

    @Transactional
    public CreateFriendRequestResponse create(
            CreateFriendRequestRequest request,
            MemberDetails memberDetails
    ) {
        Long requesterId = memberDetails.id();
        Long addresseeId = request.memberId();
        if (addresseeId.equals(requesterId)) {
            throw new CommonException(INVALID_FRIEND_REQUEST);
        }
        return friendCommandService.create(addresseeId, requesterId);
    }

    @Transactional
    public void cancel(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        FriendRelation friendRelation = friendQueryService.getFriendRelation(friendRelationId);
        if (!friendRelation.isPending()) {
            throw new CommonException(ALREADY_ACCEPTED);
        }
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendCommandService.deleteFriendRequest(friendRelationId);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }

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
        FriendRelation friendRelation = friendQueryService.getFriendRelation(friendRelationId);
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
        FriendRelation friendRelation = friendQueryService.getFriendRelation(friendRelationId);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendCommandService.deleteFriend(friendRelationId, memberDetails);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }
}
