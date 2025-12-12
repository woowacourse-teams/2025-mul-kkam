package backend.mulkkam.outboxnotification.repository;

import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxNotificationRepository extends JpaRepository<OutboxNotification, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM outbox_notification
                    WHERE status IN ('READY', 'RETRY')
                      AND (next_attempt_at IS NULL OR next_attempt_at <= NOW())
                    ORDER BY id
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxNotification> fetchReadyForSend(@Param("limit") int limit);

    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status = 'SENDING',
                   o.attemptCount = o.attemptCount + 1
             WHERE o.id = :id
            """)
    void markSending(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status = 'SENT'
             WHERE o.id = :id
            """)
    void markSent(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status =
                    CASE
                        WHEN o.attemptCount < :maxRetryCount THEN 'RETRY'
                        ELSE 'FAIL'
                    END,
                   o.nextAttemptAt = :nextAttemptAt,
                   o.lastError = :reason
             WHERE o.id = :id
            """)
    void markRetryOrFail(@Param("id") Long id,
                         @Param("nextAttemptAt") LocalDateTime nextAttemptAt,
                         @Param("reason") String reason,
                         @Param("maxRetryCount") int maxRetryCount
    );


    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status = 'FAIL',
                   o.lastError = :reason
             WHERE o.id = :id
            """)
    void markFail(@Param("id") Long id,
                  @Param("reason") String reason);
}
