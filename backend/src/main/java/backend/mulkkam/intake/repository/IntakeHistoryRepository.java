package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {

    boolean existsByMemberAndHistoryDate(Member member, LocalDate date);

    List<IntakeHistory> findAllByMember(Member member);

    @Query("SELECT DISTINCT h FROM IntakeHistory h " +
            "LEFT JOIN FETCH h.intakeHistoryDetails d " +
            "WHERE h.member = :member " +
            "AND h.historyDate BETWEEN :from AND :to ")
    List<IntakeHistory> findAllByMemberAndDateRangeWithDetails(
            Member member,
            LocalDate from,
            LocalDate to
    );

    Optional<IntakeHistory> findByMemberAndHistoryDate(Member member, LocalDate date);

    void deleteAllByMember(Member member);
}
