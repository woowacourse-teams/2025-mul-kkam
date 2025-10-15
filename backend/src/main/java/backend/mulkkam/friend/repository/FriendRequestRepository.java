package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Modifying
    @Query("""
        DELETE
        FROM FriendRequest fr
        WHERE fr.requesterId = :memberId OR fr.addresseeId = :memberId
    """)
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
