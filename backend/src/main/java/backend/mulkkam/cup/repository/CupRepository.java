package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.Cup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupRepository extends JpaRepository<Cup, Long> {
}
