package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.repository.dto.MemberInfoOfFriendRelation;
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

    @Query("""
                SELECT new backend.mulkkam.friend.repository.dto.MemberInfoOfFriendRelation(
                            fr.id,
                            m.id,
                            m.memberNickname.value
                )
                FROM FriendRelation fr
                JOIN Member m
                    ON (fr.addresseeId = :memberId AND fr.requesterId = m.id)
                    OR (fr.requesterId = :memberId AND fr.addresseeId = m.id)
                WHERE (:lastId IS NULL OR fr.id < :lastId)
                            AND fr.friendRelationStatus = 'ACCEPTED'
                ORDER BY fr.id DESC
            """)
    List<MemberInfoOfFriendRelation> findByMemberId(
            @Param("memberId") Long memberId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
