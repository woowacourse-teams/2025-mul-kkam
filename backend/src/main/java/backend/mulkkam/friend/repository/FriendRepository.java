package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
                SELECT COUNT(f) > 0 
                FROM Friend f 
                WHERE (f.requesterId = :requesterId AND f.addresseeId = :addresseeId)
                   OR (f.requesterId = :addresseeId AND f.addresseeId = :requesterId)
            """)
    boolean existsFriendByRequesterIdAndAddresseeId(@Param("requesterId") Long requesterId, @Param("addresseeId") Long addresseeId);
}
