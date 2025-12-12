package backend.mulkkam.outboxnotification.repository;

import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxNotificationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public OutboxNotificationJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchSaveIgnoringDuplicate(List<OutboxNotification> outboxList) {
        if (outboxList.isEmpty()) {
            return;
        }
        String sql = """
                INSERT INTO outbox_notification 
                (member_id, token, title, body, action, type, status, idempotency_key)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE id = id
                """;

        jdbcTemplate.batchUpdate(sql, outboxList, outboxList.size(),
                (ps, outbox) -> {
                    ps.setLong(1, outbox.getMemberId());
                    ps.setString(2, outbox.getToken());
                    ps.setString(3, outbox.getTitle());
                    ps.setString(4, outbox.getBody());
                    ps.setString(5, outbox.getAction().name());
                    ps.setString(6, outbox.getType().name());
                    ps.setString(7, outbox.getStatus().name());
                    ps.setString(8, outbox.getIdempotencyKey());
                });
    }
}
