package backend.mulkkam.friend.service.query;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.common.utils.paging.PagingUtils;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.response.FriendRelationRequestResponse;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.friend.repository.dto.MemberInfoOfFriendRelation;
import backend.mulkkam.friend.repository.dto.SentFriendRelationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendQueryService {

    private final FriendRelationRepository friendRelationRepository;

    public Optional<FriendRelation> getFriendRelation(Long requesterId, Long addresseeId) {
        return friendRelationRepository.findByRequesterIdAndAddresseeId(requesterId, addresseeId);
    }

    public Long getReceivedFriendRequestCount(MemberDetails memberDetails) {
        return friendRelationRepository.countFriendRequestsByAddresseeId(memberDetails.id());
    }

    public PagingResult<FriendRelationRequestResponse, Long> getFriendRequestsByPaging(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        Pageable pageable = PagingUtils.createPageRequest(size);
        List<FriendRelationRequestResponse> friendRelationRequestResponses = friendRelationRepository
                .findReceivedFriendRequestsAfterId(memberDetails.id(), lastId, pageable);

        return PagingUtils.toPagingResult(
                friendRelationRequestResponses,
                size,
                Function.identity(),
                FriendRelationRequestResponse::friendRequestId
        );
    }

    public PagingResult<FriendRelationResponse.MemberInfo, Long> getMemberInfosByPaging(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        List<MemberInfoOfFriendRelation> memberInfoOfFriendRelations = friendRelationRepository.findByMemberId(
                memberDetails.id(),
                lastId,
                PagingUtils.createPageRequest(size)
        );

        return PagingUtils.toPagingResult(
                memberInfoOfFriendRelations,
                size,
                FriendRelationResponse.MemberInfo::new,
                MemberInfoOfFriendRelation::friendRelationId
        );
    }

    public PagingResult<ReadSentFriendRelationResponse.SentFriendRelationInfo, Long> getSentFriendRelationInfosByPaging(
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

        return PagingUtils.toPagingResult(
                sentFriendRelationSummaries,
                size,
                ReadSentFriendRelationResponse.SentFriendRelationInfo::new,
                SentFriendRelationSummary::friendRequestId
        );
    }
}
