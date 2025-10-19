package backend.mulkkam.friend.service;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.utils.paging.PagingResult;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class FriendService {

    private final FriendCommandService friendCommandService;
    private final FriendQueryService friendQueryService;

    @Transactional
    public void delete(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        friendCommandService.deleteFriend(friendRelationId, memberDetails);
    }

    @Transactional(readOnly = true)
    public FriendRelationResponse read(
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
}
