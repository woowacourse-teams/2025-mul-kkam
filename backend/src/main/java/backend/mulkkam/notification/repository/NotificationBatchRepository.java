package backend.mulkkam.notification.repository;

import backend.mulkkam.notification.dto.NotificationInsertDto;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class NotificationBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<NotificationInsertDto> notificationInsertDtos, int batchSize) {

        // TODO: 제거
        log.info("Inserting {} notifications into batch", notificationInsertDtos.size());

        String sql = "INSERT INTO notification (notification_type, is_read, created_at, member_id, content, deleted_at) values (?, ?, ?, ?, ?, ?)";
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());

        for (int i = 0; i < notificationInsertDtos.size(); i += batchSize) {
            List<NotificationInsertDto> batchNotificationInsertDtos = notificationInsertDtos.subList(i,
                    Math.min(i + batchSize, notificationInsertDtos.size()));

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    NotificationInsertDto notificationInsertDto = batchNotificationInsertDtos.get(i);
                    ps.setString(1, notificationInsertDto.notificationType().name());
                    ps.setBoolean(2, Boolean.FALSE);
                    ps.setTimestamp(3, currentTimestamp);
                    ps.setLong(4, notificationInsertDto.memberId());
                    ps.setString(5, notificationInsertDto.content());
                    ps.setTimestamp(6, null);
                }

                @Override
                public int getBatchSize() {
                    return batchNotificationInsertDtos.size();
                }
            });
        }
    }
}
