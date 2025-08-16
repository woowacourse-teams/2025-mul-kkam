package backend.mulkkam.notification.repository;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionNotificationRepository extends JpaRepository<SuggestionNotification, Long> {

    SuggestionNotification getSuggestionNotificationByNotification(Notification notification);
}
