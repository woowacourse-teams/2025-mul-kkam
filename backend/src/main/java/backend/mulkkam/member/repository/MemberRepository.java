package backend.mulkkam.member.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.response.MemberSearchItemResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByActiveNickname(String activeNickname);

    @Query("""
                select m.id
                from Member m
                order by m.id
            """)
    Slice<Long> findIdsBySlice(Pageable pageable);

    @Query("""
            select m.id
            from Member m
            where (:lastId is null or m.id > :lastId)
            order by m.id
            """)
    List<Long> findIdsAfter(
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
            SELECT new backend.mulkkam.member.dto.response.MemberSearchItemResponse(
              m.id,
              m.memberNickname.value,
              CASE
                WHEN fr.id IS NULL
                  THEN backend.mulkkam.friend.domain.FriendStatus.NONE
                ELSE fr.friendStatus
              END,
              CASE
                WHEN fr.id IS NULL
                  THEN backend.mulkkam.friend.domain.RequestDirection.NONE
                WHEN fr.friendStatus = backend.mulkkam.friend.domain.FriendStatus.REQUESTED
                     AND fr.requesterId = :id
                  THEN backend.mulkkam.friend.domain.RequestDirection.REQUESTED_BY_ME
                WHEN fr.friendStatus = backend.mulkkam.friend.domain.FriendStatus.REQUESTED
                     AND fr.addresseeId = :id
                  THEN backend.mulkkam.friend.domain.RequestDirection.REQUESTED_TO_ME
                ELSE backend.mulkkam.friend.domain.RequestDirection.NONE
              END
            )
            FROM Member m
            LEFT JOIN FriendRelation fr
               ON (
                 (fr.requesterId = :id AND fr.addresseeId = m.id) OR
                 (fr.requesterId = m.id AND fr.addresseeId = :id)
               )
              AND fr.deletedAt IS NULL
            WHERE (:prefix <> '' AND m.memberNickname.value LIKE CONCAT(:prefix, '%'))
              AND m.id <> :id
            ORDER BY m.memberNickname.value
            """)
    Slice<MemberSearchItemResponse> findByNicknamePrefixWithStatusAndDirection(
            @Param("id") Long id,
            @Param("prefix") String prefix,
            Pageable pageable
    );

}
