package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {

    boolean existsByMemberAndHistoryDate(Member member, LocalDate date);

    List<IntakeHistory> findAllByMember(Member member);

    List<IntakeHistory> findAllByMemberAndHistoryDateBetween(
            Member member,
            LocalDate dateAfter,
            LocalDate dateBefore
    );

    Optional<IntakeHistory> findByMemberAndHistoryDate(Member member, LocalDate date);

    void deleteAllByMember(Member member);

    @Query("""
                SELECT h
                FROM IntakeHistory h
                WHERE h.member = :member
                    AND h.historyDate BETWEEN :from AND :to
            """)
    List<IntakeHistory> findAllByMemberAndDateRange(
            @Param("member") Member member,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
