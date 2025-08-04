package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.Cup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CupRepository extends JpaRepository<Cup, Long> {

    int countByMemberId(Long memberId);

    Optional<Cup> findByIdAndMemberId(Long id, Long memberId);

    List<Cup> findAllByMemberId(Long memberId);

    List<Cup> findAllByMemberIdOrderByCupRankAsc(Long memberId);
}
