package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.AdminFriendRelationListResponse;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminFriendRelationService {

    private final FriendRelationRepository friendRelationRepository;

    @Transactional(readOnly = true)
    public Page<AdminFriendRelationListResponse> getFriendRelations(Pageable pageable) {
        return friendRelationRepository.findAll(pageable)
                .map(AdminFriendRelationListResponse::from);
    }

    @Transactional
    public void deleteFriendRelation(Long friendRelationId) {
        FriendRelation friendRelation = friendRelationRepository.findById(friendRelationId)
                .orElseThrow(() -> new IllegalArgumentException("FriendRelation not found: " + friendRelationId));
        friendRelationRepository.delete(friendRelation);
    }
}
