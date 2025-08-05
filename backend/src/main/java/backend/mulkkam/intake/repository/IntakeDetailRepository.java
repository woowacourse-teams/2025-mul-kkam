package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeDetail;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntakeDetailRepository extends JpaRepository<IntakeDetail, Long> {
    
    @Query("SELECT d FROM IntakeDetail d " +
            "JOIN d.intakeHistory h " +
            "WHERE h.member.id = :memberId " +
            "AND h.historyDate BETWEEN :dateAfter AND :dateBefore " +
            "ORDER BY h.historyDate")
    List<IntakeDetail> findAllByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("dateAfter") LocalDate dateAfter,
            @Param("dateBefore") LocalDate dateBefore
    );
}
