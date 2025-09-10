package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
    List<IntakeHistory> findAllByMember(Member member);

    List<IntakeHistory> findAllByMemberAndHistoryDateBetween(
            Member member,
            LocalDate dateAfter,
            LocalDate dateBefore
    );

    Optional<IntakeHistory> findByMemberAndHistoryDate(Member member, LocalDate date);

    void deleteByMember(Member member);
}
