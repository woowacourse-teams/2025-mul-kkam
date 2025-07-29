package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
    List<IntakeHistory> findAllByMemberId(Long memberId);

    List<IntakeHistory> findAllByMemberIdAndDateTimeBetween(
            Long memberId,
            LocalDateTime dateTimeAfter,
            LocalDateTime dateTimeBefore
    );

    @Query(value = """
            SELECT COUNT(*) AS consecutive_days
            FROM (
                SELECT 
                    DATE(date_time) AS record_date,
                    ROW_NUMBER() OVER (ORDER BY DATE(date_time) DESC) AS rn
                FROM intake_history
                WHERE member_id = :memberId
                  AND DATE(date_time) <= :baseDate
                GROUP BY DATE(date_time)
            ) AS sub
            WHERE DATEDIFF(:baseDate, record_date) = rn - 1
            """, nativeQuery = true)
    int countConsecutiveDays(@Param("memberId") Long memberId,
                             @Param("baseDate") LocalDate baseDate);
}
