package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
                SELECT COUNT(f) > 0 
                FROM Friend f 
                WHERE (f.requesterId = :requesterId AND f.addresseeId = :addresseeId)
                   OR (f.requesterId = :addresseeId AND f.addresseeId = :requesterId)
            """)
    boolean existsFriendByRequesterIdAndAddresseeId(@Param("requesterId") Long requesterId,
                                                    @Param("addresseeId") Long addresseeId);

    @Query("""
                    SELECT f
                    FROM Friend f
                    WHERE f.addresseeId = :friendId AND f.requesterId = :memberId
                        OR f.addresseeId = :memberId AND f.requesterId = :friendId
            """)
    Optional<Friend> findByFriendIdAndMemberId(@Param("friendId") Long friendId, @Param("memberId") Long memberId);

    @Modifying
    @Query("""
        DELETE
        FROM Friend f
        WHERE f.addresseeId = :memberId OR f.requesterId = :memberId
    """)
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
