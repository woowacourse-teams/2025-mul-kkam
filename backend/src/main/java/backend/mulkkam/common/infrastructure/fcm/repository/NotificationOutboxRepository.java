package backend.mulkkam.common.infrastructure.fcm.repository;

import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {

    @Query(
            value = """
                    select *
                    from notification_outbox
                    where status = :status
                      and next_attempt_at <= :now
                    order by id
                    limit :limit
                    for update skip locked
                    """,
            nativeQuery = true
    )
    List<NotificationOutbox> findPendingForUpdate(
            @Param("status") String status,
            @Param("now") LocalDateTime now,
            @Param("limit") int limit
    );
}
