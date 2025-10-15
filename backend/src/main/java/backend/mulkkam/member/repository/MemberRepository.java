package backend.mulkkam.member.repository;

import backend.mulkkam.member.domain.Member;
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
}
