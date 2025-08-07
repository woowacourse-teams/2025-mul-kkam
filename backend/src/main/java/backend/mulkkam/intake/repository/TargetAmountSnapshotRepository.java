package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetAmountSnapshotRepository extends JpaRepository<TargetAmountSnapshot, Long> {
}
