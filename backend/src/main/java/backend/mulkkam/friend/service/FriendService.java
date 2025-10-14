package backend.mulkkam.friend.service;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;

    public void delete(Long memberId, MemberDetails memberDetails) {
        friendRepository.findByFriendIdAndMemberId(memberId, memberDetails.id())
                .ifPresent(friendRepository::delete);
    }
}
