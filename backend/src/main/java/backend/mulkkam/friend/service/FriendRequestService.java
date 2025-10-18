package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.ALREADY_ACCEPTED;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.request.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.response.CreateFriendRequestResponse;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.FriendRelationRequestResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class FriendRequestService {

    private final FriendQueryService friendQueryService;
    private final FriendCommandService friendCommandService;

    @Transactional(readOnly = true)
    public ReadReceivedFriendRelationResponse readReceivedFriendRequests(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        PagingResult<FriendRelationRequestResponse, Long> pagingResult = friendQueryService.getFriendRequestsByPaging(memberDetails, lastId, size);
        return new ReadReceivedFriendRelationResponse(
                pagingResult.content(),
                pagingResult.nextCursor(),
                pagingResult.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public GetReceivedFriendRequestCountResponse getReceivedFriendRequestCount(MemberDetails memberDetails) {
        Long count = friendQueryService.getReceivedFriendRequestCount(memberDetails);
        return new GetReceivedFriendRequestCountResponse(count);
    }

    @Transactional(readOnly = true)
    public ReadSentFriendRelationResponse readSentFriendRelations(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        PagingResult<ReadSentFriendRelationResponse.SentFriendRelationInfo, Long> pagingResult
                = friendQueryService.getSentFriendRelationInfosByPaging(memberDetails, lastId, size);

        return new ReadSentFriendRelationResponse(
                pagingResult.content(),
                pagingResult.nextCursor(),
                pagingResult.hasNext()
        );
    }

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
        FriendRelation friendRelation = getModifiableRelation(friendRelationId);
        if (friendRelation.isRequesterMemberId(memberDetails.id())) {
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
        FriendRelation friendRelation = getModifiableRelation(friendRelationId);
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
        FriendRelation friendRelation = getModifiableRelation(friendRelationId);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendCommandService.deleteFriendRequest(friendRelationId);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }

    private FriendRelation getModifiableRelation(Long friendRelationId) {
        FriendRelation friendRelation = friendQueryService.getFriendRelation(friendRelationId);
        if (!friendRelation.isPending()) {
            throw new CommonException(ALREADY_ACCEPTED);
        }
        return friendRelation;
    }
}
