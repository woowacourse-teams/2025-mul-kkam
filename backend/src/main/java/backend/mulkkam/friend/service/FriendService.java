package backend.mulkkam.friend.service;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.dto.response.FriendRequestResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRequestsResponse;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.repository.MemberRepository;
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

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void delete(Long memberId, MemberDetails memberDetails) {
        friendRepository.findByFriendIdAndMemberId(memberId, memberDetails.id())
                .ifPresent(friendRepository::delete);
    }

    public ReadReceivedFriendRequestsResponse readReceivedFriendRequests(
            MemberDetails memberDetails,
            Long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<FriendRequestResponse> friendRequestResponses = friendRequestRepository
                .findReceivedFriendRequestsAfterId(memberDetails.id(), lastId, pageable);
        return ReadReceivedFriendRequestsResponse.of(friendRequestResponses, size);
    }

    public GetReceivedFriendRequestCountResponse getReceivedFriendRequestCount(MemberDetails memberDetails) {
        return new GetReceivedFriendRequestCountResponse(
                friendRequestRepository.countFriendRequestsByAddresseeId(memberDetails.id()));
    }
}
