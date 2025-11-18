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
        String sql = """
                INSERT INTO outbox_notification 
                (type, member_id, token, title, body, status, dedupe_key)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE id = id
                """;

        jdbcTemplate.batchUpdate(sql, outboxList, outboxList.size(),
                (ps, outbox) -> {
                    ps.setString(1, outbox.getType());
                    ps.setLong(2, outbox.getMemberId());
                    ps.setString(3, outbox.getToken());
                    ps.setString(4, outbox.getTitle());
                    ps.setString(5, outbox.getBody());
                    ps.setString(6, outbox.getStatus().name());
                    ps.setString(7, outbox.getDedupeKey());
                });
    }
}
