package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
    SELECT n
    FROM Notification n
    WHERE n.id < :lastId
      AND n.createdAt >= :limitStartDateTime
      AND n.member = :member
    ORDER BY n.id DESC
    """)
    List<Notification> findByCursor(
            Member member,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    @Query("""
    SELECT n
    FROM Notification n
    WHERE n.createdAt >= :limitStartDateTime
      AND n.member = :member
    ORDER BY n.id DESC
    """)
    List<Notification> findLatest(
            Member member,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    void deleteByMember(Member member);

    Long countByIsReadFalseAndMember(Member member);
}
