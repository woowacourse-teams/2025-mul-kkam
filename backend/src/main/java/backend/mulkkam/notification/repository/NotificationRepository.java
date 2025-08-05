package backend.mulkkam.notification.repository;

import backend.mulkkam.notification.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE (n.id < :lastId and n.createdAt > :limitStartDateTime) ORDER BY n.id DESC")
    List<Notification> findByCursor(Long lastId, LocalDateTime limitStartDateTime, Pageable pageable);
}
