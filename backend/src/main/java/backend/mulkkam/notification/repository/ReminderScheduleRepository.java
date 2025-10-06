package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.ReminderSchedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, Long> {

    List<ReminderSchedule> findAllByMemberOrderByScheduleAsc(Member member);

    @Query("""
            select rs
            from ReminderSchedule rs
            where rs.id = :id
              and rs.member.id = :memberId
              and rs.deletedAt is null
            """)
    Optional<ReminderSchedule> findByIdAndMemberId(Long id, Long memberId);

    boolean existsByIdAndMemberId(Long id, Long memberId);
}
