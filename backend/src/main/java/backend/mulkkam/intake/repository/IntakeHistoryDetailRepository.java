package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.member.domain.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IntakeHistoryDetailRepository extends JpaRepository<IntakeHistoryDetail, Long> {

    @Override
    @Query("""
                SELECT d
                FROM IntakeHistoryDetail d
                JOIN FETCH d.intakeHistory h
                JOIN FETCH h.member
                WHERE d.id = :id
            """)
    Optional<IntakeHistoryDetail> findById(@NotNull @Param("id") Long id);

    @Query("""
                SELECT d
                FROM IntakeHistoryDetail d
                JOIN FETCH d.intakeHistory h
                JOIN FETCH h.member
                WHERE h.member = :member
                    AND h.historyDate BETWEEN :from AND :to
            """)
    List<IntakeHistoryDetail> findAllByMemberAndDateRange(
            @Param("member") Member member,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    void deleteAllByIntakeHistoryIn(List<IntakeHistory> intakeHistory);

    List<IntakeHistoryDetail> findByIntakeHistory(IntakeHistory intakeHistory);
}
