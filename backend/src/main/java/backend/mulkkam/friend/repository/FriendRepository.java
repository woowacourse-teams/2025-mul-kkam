package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
            SELECT f
            FROM Friend f
            WHERE f.addresseeId = :friendId AND f.requesterId = :memberId
                OR f.addresseeId = :memberId AND f.requesterId = :friendId
    """)
    Optional<Friend> findByFriendIdAndMemberId(@Param("friendId") Long friendId, @Param("memberId") Long memberId);
}
