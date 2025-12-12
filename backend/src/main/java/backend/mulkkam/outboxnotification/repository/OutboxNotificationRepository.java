package backend.mulkkam.outboxnotification.repository;

import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxNotificationRepository extends JpaRepository<OutboxNotification, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM outbox_notification
                    WHERE status IN ('READY', 'RETRY')
                      AND (next_attempt_at IS NULL OR next_attempt_at <= NOW())
                      AND deleted_at is null
                    ORDER BY id
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxNotification> fetchReadyForSend(@Param("limit") int limit);
}
