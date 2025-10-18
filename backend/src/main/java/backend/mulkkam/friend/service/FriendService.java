package backend.mulkkam.friend.service;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class FriendService {

    private final FriendCommandService friendCommandService;
    private final FriendQueryService friendQueryService;

    @Transactional
    public void deleteFriend(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        friendCommandService.deleteFriend(friendRelationId, memberDetails);
    }

    @Transactional(readOnly = true)
    public FriendRelationResponse readFriendRelationsInStatusAccepted(
            Long lastId,
            int size,
            MemberDetails memberDetails
    ) {
        PagingResult<FriendRelationResponse.MemberInfo, Long> pagingResult = friendQueryService.getMemberInfosByPaging(
                memberDetails, lastId, size
        );

        return new FriendRelationResponse(
                pagingResult.content(),
                pagingResult.nextCursor(),
                pagingResult.hasNext()
        );
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
}
