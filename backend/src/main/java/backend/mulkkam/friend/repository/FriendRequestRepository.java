package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRequest;
import backend.mulkkam.friend.dto.response.FriendRequestResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("""
                SELECT new backend.mulkkam.friend.dto.response.FriendRequestResponse(fr.id, m.memberNickname.value)
                FROM FriendRequest fr
                JOIN Member m ON fr.requesterId = m.id
                WHERE fr.requesterId = :receiverId
                  AND (:lastId IS NULL OR fr.id < :lastId)
                ORDER BY fr.id DESC
            """)
    List<FriendRequestResponse> findReceivedFriendRequestsAfterId(
            @Param("receiverId") Long receiverId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
                SELECT COUNT(fr)
                FROM FriendRequest fr
                WHERE fr.addresseeId = :addresseeId
            """)
    Long countFriendRequestsByAddresseeId(@Param("addresseeId") Long addresseeId);

}
