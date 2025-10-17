package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_RELATION;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.common.utils.paging.PagingUtils;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse.SentFriendRelationInfo;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.friend.repository.dto.SentFriendRelationSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {

    private final FriendRelationRepository friendRelationRepository;

    @Transactional
    public void delete(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        friendRelationRepository.findByIdAndMemberId(friendRelationId, memberDetails.id())
                .ifPresent(friendRelationRepository::delete);
    }

    @Transactional
    public void acceptFriend(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        FriendRelation friendRelation = getFriendRelation(friendRelationId);
        validRequested(friendRelation);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendRelation.updateAccepted();
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);

    }

    @Transactional
    public void rejectFriendRequest(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        FriendRelation friendRelation = getFriendRelation(friendRelationId);
        validRequested(friendRelation);
        if (friendRelation.isAddresseeMemberId(memberDetails.id())) {
            friendRelationRepository.delete(friendRelation);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }

    public ReadReceivedFriendRelationResponse readReceivedFriendRequests(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<FriendRelationResponse> friendRequestResponses = friendRelationRepository
                .findReceivedFriendRequestsAfterId(memberDetails.id(), lastId, pageable);
        return ReadReceivedFriendRelationResponse.of(friendRequestResponses, size);
    }


    public GetReceivedFriendRequestCountResponse getReceivedFriendRequestCount(MemberDetails memberDetails) {
        return new GetReceivedFriendRequestCountResponse(
                friendRelationRepository.countFriendRequestsByAddresseeId(memberDetails.id()));
    }

    public ReadSentFriendRelationResponse readSentFriendRelations(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        PageRequest pageRequest = PagingUtils.createPageRequest(size);

        List<SentFriendRelationSummary> sentFriendRelationSummaries = friendRelationRepository.findSentFriendRelationsAfterId(
                memberDetails.id(),
                lastId,
                pageRequest
        );

        PagingResult<SentFriendRelationInfo, Long> pagingResult = PagingUtils.toPagingResult(
                sentFriendRelationSummaries,
                size,
                SentFriendRelationInfo::new,
                SentFriendRelationSummary::friendRequestId
        );

        return new ReadSentFriendRelationResponse(
                pagingResult.content(),
                pagingResult.nextCursor(),
                pagingResult.hasNext()
        );
    }

    private FriendRelation getFriendRelation(Long friendRelationId) {
        return friendRelationRepository.findById(friendRelationId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FRIEND_RELATION));
    }

    private void validRequested(final FriendRelation friendRelation) {
        if (friendRelation.isNotRequest()) {
            throw new CommonException(INVALID_FRIEND_RELATION);
        }
    }
}
