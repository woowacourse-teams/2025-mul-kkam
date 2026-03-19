package backend.mulkkam.admin.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND;

import backend.mulkkam.admin.dto.response.GetAdminFriendRelationListResponse;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminFriendRelationService {

    private final FriendRelationRepository friendRelationRepository;

    public Page<GetAdminFriendRelationListResponse> getFriendRelations(Pageable pageable) {
        return friendRelationRepository.findAll(pageable)
                .map(GetAdminFriendRelationListResponse::from);
    }

    @Transactional
    public void deleteFriendRelation(Long friendRelationId) {
        FriendRelation friendRelation = friendRelationRepository.findById(friendRelationId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FRIEND));
        friendRelationRepository.delete(friendRelation);
    }
}
