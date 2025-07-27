package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.Cup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupRepository extends JpaRepository<Cup, Long> {

    List<Cup> findAllByMemberId(Long memberId);

    List<Cup> findAllByMemberIdOrderByCupRankAsc(Long memberId);
}
