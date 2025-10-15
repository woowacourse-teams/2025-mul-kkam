package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE
        FROM FriendRequest fr
        WHERE fr.requesterId = :memberId OR fr.addresseeId = :memberId
    """)
    void deleteByMemberId(@Param("memberId") Long memberId);
}
