package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeDetailRepository extends JpaRepository<IntakeDetail, Long> {
    
    List<IntakeDetail> findAllByIntakeHistoryIdIn(List<Long> historyIds);
}
