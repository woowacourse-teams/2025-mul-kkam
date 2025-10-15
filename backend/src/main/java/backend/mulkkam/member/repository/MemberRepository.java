package backend.mulkkam.member.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.response.MemberIdNicknameResponse;
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
            SELECT new backend.mulkkam.member.dto.response.MemberIdNicknameResponse(
              m.id,
              m.memberNickname.value,
              CASE
                WHEN fr.id IS NULL
                  THEN backend.mulkkam.friend.domain.FriendStatus.NONE
                ELSE fr.friendStatus
              END
            )
            FROM Member m
            LEFT JOIN FriendRelation fr
               ON (
                 (fr.requesterId = :id AND fr.addresseeId = m.id) OR
                 (fr.requesterId = m.id AND fr.addresseeId = :id)
               )
              AND fr.deletedAt IS NULL
            WHERE m.memberNickname.value LIKE CONCAT(:prefix, '%')
              AND m.id <> :id
            ORDER BY m.memberNickname.value
            """)
    Slice<MemberIdNicknameResponse> findByNicknamePrefixWithStatus(
            @Param("id") Long id,
            @Param("prefix") String prefix,
            Pageable pageable
    );

}
