package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
}
