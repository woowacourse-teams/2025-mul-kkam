package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRelation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {

    @Query("""
            SELECT fr
            FROM FriendRelation fr
            WHERE fr.id = :id
                AND (fr.addresseeId = :memberId OR fr.requesterId = :memberId)
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
}
