package backend.mulkkam.outboxnotification.repository;

import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxNotificationRepository extends JpaRepository<OutboxNotification, Long> {

    /**
     * READY 상태이면서 next_attempt_at <= NOW() 인 레코드를 LIMIT 만큼 가져옴. - SKIP LOCKED 로 다른 dispatcher 실행과 충돌 방지
     */
    @Query(
            value = """
                    SELECT *
                    FROM outbox_notification
                    WHERE status = 'READY'
                      AND (next_attempt_at IS NULL OR next_attempt_at <= NOW())
                    ORDER BY id
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxNotification> fetchReadyForSend(@Param("limit") int limit);


    /**
     * SENDING 상태로 변경. attemptCount += 1
     */
    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status = 'SENDING',
                   o.attemptCount = o.attemptCount + 1
             WHERE o.id = :id
            """)
    void markSending(@Param("id") Long id);


    /**
     * 성공 처리 (SENT)
     */
    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status = 'SENT'
             WHERE o.id = :id
            """)
    void markSent(@Param("id") Long id);


    /**
     * 실패 처리 (Retry or Fail) attempts < 8 → RETRY attempts >= 8 → FAIL
     */
    @Modifying
    @Query("""
            UPDATE OutboxNotification o
               SET o.status =
                    CASE
                        WHEN o.attemptCount + 1 < 8 THEN 'RETRY'
                        ELSE 'FAIL'
                    END,
                   o.attemptCount = o.attemptCount + 1,
                   o.nextAttemptAt = :nextAttemptAt,
                   o.lastError = :reason
             WHERE o.id = :id
            """)
    void markRetryOrFail(@Param("id") Long id,
                         @Param("nextAttemptAt") LocalDateTime nextAttemptAt,
                         @Param("reason") String reason);


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
