package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.ReminderSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, Long> {

    List<ReminderSchedule> findAllByMember(Member member);
}
