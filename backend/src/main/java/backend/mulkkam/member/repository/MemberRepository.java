package backend.mulkkam.member.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.dto.MemberSearchRow;
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
            SELECT new backend.mulkkam.member.repository.dto.MemberSearchRow(
                m.id,
                m.memberNickname.value,
                (
                  SELECT fr.friendStatus
                  FROM FriendRelation fr
                  WHERE fr.deletedAt IS NULL
                    AND (
                         (fr.requesterId = :id AND fr.addresseeId = m.id)
                      OR (fr.requesterId = m.id AND fr.addresseeId = :id)
                    )
                ),
                CASE WHEN EXISTS (
                  SELECT 1
                  FROM FriendRelation fr2
                  WHERE fr2.deletedAt IS NULL
                    AND fr2.requesterId = :id
                    AND (fr2.addresseeId = m.id)
                ) THEN true ELSE false END
            )
            FROM Member m
            WHERE (:word <> '' AND m.memberNickname.value LIKE CONCAT(:word, '%'))
              AND m.id <> :id
            ORDER BY m.memberNickname.value
            """)
    Slice<MemberSearchRow> searchByWord(
            @Param("id") Long id,
            @Param("word") String word,
            Pageable pageable);
}
