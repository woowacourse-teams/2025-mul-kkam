package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRelation;
import java.util.Collection;
import java.util.List;
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
                AND fr.deletedAt is null
                AND fr.addresseeId = :memberId OR fr.requesterId = :memberId
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
                SELECT fr
                FROM FriendRelation fr
                WHERE fr.deletedAt IS NULL
                  AND (
                       (fr.requesterId = :memberId AND fr.addresseeId IN :memberIds)
                    OR (fr.addresseeId = :memberId AND fr.requesterId IN :memberIds)
                  )
            """)
    List<FriendRelation> findByMemberIdAndInIds(
            @Param("memberId") Long memberId, @Param("memberIds") Collection<Long> memberIds);
}
