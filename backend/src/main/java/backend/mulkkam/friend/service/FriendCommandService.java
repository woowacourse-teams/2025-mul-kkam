package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.friend.domain.FriendRelationStatus.REQUESTED;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.CreateFriendRequestResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendCommandService {

    private final FriendRelationRepository friendRelationRepository;
    private final MemberRepository memberRepository;

    public void delete(
            Long friendRelationId,
            MemberDetails memberDetails
    ) {
        friendRelationRepository.findByIdAndMemberId(friendRelationId, memberDetails.id())
                .ifPresent(friendRelationRepository::delete);
    }

    public FriendRelation create(FriendRelation friendRelation) {
        return friendRelationRepository.save(friendRelation);
    }

    public CreateFriendRequestResponse create(
            Long addresseeId,
            Long requesterId
    ) {
        if (!memberRepository.existsById(addresseeId)) {
            throw new CommonException(NOT_FOUND_MEMBER);
        }

        if (friendRelationRepository.existsByMemberIds(addresseeId, requesterId)) {
            throw new CommonException(DUPLICATED_FRIEND_REQUEST);
        }
        FriendRelation friendRelation = new FriendRelation(requesterId, addresseeId, REQUESTED);
        friendRelationRepository.save(friendRelation);
        return new CreateFriendRequestResponse(friendRelation);
    }
}
