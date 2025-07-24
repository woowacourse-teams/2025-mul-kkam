package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
    List<IntakeHistory> findAllByMemberId(Long memberId);

    List<IntakeHistory> findAllByMemberIdAndDateTimeBetween(
            Long memberId,
            LocalDateTime dateTimeAfter,
            LocalDateTime dateTimeBefore
    );
}
