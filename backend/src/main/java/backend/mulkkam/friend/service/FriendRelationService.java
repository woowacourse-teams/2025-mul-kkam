package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_RELATION;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.common.utils.paging.PagingUtils;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.FriendRelationResponse;
import backend.mulkkam.friend.dto.FriendRelationResponse.MemberInfo;
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.FriendRelationRequestResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.friend.repository.dto.MemberInfoOfFriendRelation;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendRelationService {

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

    // TODO: endpoint 변경 PR 반영되면 접근 제어를 private 으로 변경하기
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
        validatePageSize(size);
        Pageable pageable = PagingUtils.createPageRequest(size);
        List<FriendRelationRequestResponse> friendRelationRequestResponses = friendRelationRepository
                .findReceivedFriendRequestsAfterId(memberDetails.id(), lastId, pageable);

        PagingResult<FriendRelationRequestResponse, Long> pagingResult = PagingUtils.toPagingResult(
                friendRelationRequestResponses,
                size,
                Function.identity(),
                FriendRelationRequestResponse::friendRequestId
        );
        return new ReadReceivedFriendRelationResponse(
                pagingResult.content(),
                pagingResult.nextCursor(),
                pagingResult.hasNext()
        );
    }

    public GetReceivedFriendRequestCountResponse getReceivedFriendRequestCount(MemberDetails memberDetails) {
        return new GetReceivedFriendRequestCountResponse(
                friendRelationRepository.countFriendRequestsByAddresseeId(memberDetails.id()));
    }

    public FriendRelationResponse readFriendRelationsInStatusAccepted(
            Long lastId,
            int size,
            MemberDetails memberDetails
    ) {
        validatePageSize(size);

        List<MemberInfoOfFriendRelation> memberInfoOfFriendRelations = friendRelationRepository.findByMemberId(
                memberDetails.id(),
                lastId,
                PagingUtils.createPageRequest(size)
        );

        PagingResult<MemberInfo, Long> pagingResult = PagingUtils.toPagingResult(
                memberInfoOfFriendRelations,
                size,
                MemberInfo::new,
                MemberInfoOfFriendRelation::friendRelationId
        );

        return new FriendRelationResponse(
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

    private void validatePageSize(int size) {
        if (size <= 0) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
    }
}
