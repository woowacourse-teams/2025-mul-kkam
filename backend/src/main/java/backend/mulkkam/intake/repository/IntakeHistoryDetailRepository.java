package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntakeHistoryDetailRepository extends JpaRepository<IntakeHistoryDetail, Long> {

    @Query("SELECT d FROM IntakeHistoryDetail d " +
            "JOIN d.intakeHistory h " +
            "WHERE h.member.id = :memberId " +
            "AND h.historyDate BETWEEN :dateAfter AND :dateBefore " +
            "ORDER BY h.historyDate")
    List<IntakeHistoryDetail> findAllByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("dateAfter") LocalDate dateAfter,
            @Param("dateBefore") LocalDate dateBefore
    );

    @Query("""
                SELECT d
                FROM IntakeHistoryDetail d
                JOIN FETCH d.intakeHistory h
                JOIN FETCH h.member
                WHERE d.id = :id
            """)
    Optional<IntakeHistoryDetail> findWithHistoryAndMemberById(@Param("id") Long id);
}
