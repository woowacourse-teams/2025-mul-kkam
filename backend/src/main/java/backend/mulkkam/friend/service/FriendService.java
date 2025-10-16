package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_RELATION;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import lombok.RequiredArgsConstructor;
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
        if (friendRelation.isNotRequest()) {
            throw new CommonException(INVALID_FRIEND_RELATION);
        }
        if (friendRelation.isAddressee(memberDetails.id())) {
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
        if (friendRelation.isNotRequest()) {
            throw new CommonException(INVALID_FRIEND_RELATION);
        }
        if (friendRelation.isAddressee(memberDetails.id())) {
            friendRelationRepository.delete(friendRelation);
            return;
        }
        throw new CommonException(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST);
    }

    private FriendRelation getFriendRelation(Long friendRelationId) {
        return friendRelationRepository.findById(friendRelationId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FRIEND_RELATION));
    }
}
