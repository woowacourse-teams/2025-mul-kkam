package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Integer> {
}
