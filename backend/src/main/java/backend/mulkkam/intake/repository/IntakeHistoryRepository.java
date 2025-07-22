package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
    // TODO: 단위 테스트 추가하기
    List<IntakeHistory> findAllByMemberId(Long memberId);

    List<IntakeHistory> findAllByMemberIdAndDateTimeBetween(
            Long memberId,
            LocalDateTime dateTimeAfter,
            LocalDateTime dateTimeBefore
    );
}
