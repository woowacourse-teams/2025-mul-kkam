package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.Cup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CupRepository extends JpaRepository<Cup, Long> {
    List<Cup> findAllByMemberId(Long memberId);

    @Query("SELECT MAX(c.rank) FROM Cup c WHERE c.member.id = :memberId")
    Integer findMaxRankByMemberId(Long memberId);
}
