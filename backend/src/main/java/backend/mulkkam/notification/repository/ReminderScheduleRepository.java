package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.ReminderSchedule;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
                SELECT r.member.id
                FROM ReminderSchedule r
                JOIN r.member
                WHERE HOUR(r.schedule) = HOUR(:schedule)
                  AND MINUTE(r.schedule) = MINUTE(:schedule)
                  AND r.member.isReminderEnabled = true
                  AND (:lastId IS NULL OR r.id > :lastId)
            """)
    List<Long> findAllActiveMemberIdsBySchedule(
            @Param("schedule") LocalTime schedule,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

//    @Query("""
//                SELECT m.id
//                FROM Member m
//                WHERE (:lastId IS NULL OR m.id > :lastId)
//            """)
//    List<Long> findAllActiveMemberIdsBySchedule(
//            @Param("schedule") LocalTime schedule,
//            @Param("lastId") Long lastId,
//            Pageable pageable
//    );

    void deleteAllByMemberId(Long memberId);
}
