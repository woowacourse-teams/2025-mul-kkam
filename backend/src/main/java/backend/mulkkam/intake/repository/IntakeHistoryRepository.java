package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends JpaRepository<IntakeHistory, Long> {
    List<IntakeHistory> findAllByMember(Member member);

    List<IntakeHistory> findAllByMemberAndHistoryDateBetween(
            Member member,
            LocalDate dateAfter,
            LocalDate dateBefore
    );

    Optional<IntakeHistory> findByMemberAndHistoryDate(Member member, LocalDate date);
}
