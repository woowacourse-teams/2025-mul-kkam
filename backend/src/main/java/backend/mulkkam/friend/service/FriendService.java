package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.Friend;
import backend.mulkkam.friend.domain.FriendRequest;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void processFriendRequest(
            Long friendRequestId,
            boolean isAccept,
            MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.id();
        validateMemberExistence(memberId);
        FriendRequest friendRequest = getFriendRequest(friendRequestId);

        if (!friendRequest.validatePermission(memberId)) {
            throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
        }

        if (isAccept) {
            Friend friend = new Friend(friendRequest);
            friendRepository.save(friend);
        }
        friendRequestRepository.delete(friendRequest);
    }

    private void validateMemberExistence(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new CommonException(NOT_FOUND_MEMBER);
        }
    }

    private FriendRequest getFriendRequest(Long friendRequestId) {
        return friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FRIEND_REQUEST));
    }
}