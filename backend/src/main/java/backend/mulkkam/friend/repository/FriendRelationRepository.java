package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.dto.response.FriendRequestResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {

    @Query("""
            SELECT fr
            FROM FriendRelation fr
            WHERE fr.id = :id
                AND fr.deletedAt is null
                AND (
                    (fr.addresseeId = :memberId OR fr.requesterId = :memberId)
                )
            """)
    Optional<FriendRelation> findByIdAndMemberId(
            @Param("id") Long id,
            @Param("memberId") Long memberId
    );

    @Modifying
    @Query("""
                DELETE
                FROM FriendRelation fr
                WHERE fr.addresseeId = :memberId OR fr.requesterId = :memberId
            """)
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Query("""
                SELECT new backend.mulkkam.friend.dto.response.FriendRequestResponse(fr.id, m.memberNickname.value)
                FROM FriendRelation fr
                JOIN Member m ON fr.requesterId = m.id
                WHERE fr.addresseeId = :addresseeId
                  AND (fr.friendStatus = "REQUESTED")
                  AND (:lastId IS NULL OR fr.id < :lastId)
                ORDER BY fr.id DESC
            """)
    List<FriendRequestResponse> findReceivedFriendRequestsAfterId(
            @Param("addresseeId") Long addresseeId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
                SELECT COUNT(fr)
                FROM FriendRelation fr
                WHERE fr.addresseeId = :addresseeId
                AND (fr.friendStatus = "REQUESTED")
            """)
    Long countFriendRequestsByAddresseeId(@Param("addresseeId") Long addresseeId);
}
