-- ALLOW_DROP
-- reason: reminder_schedule에는 soft delete 예정이므로 유니크키를 변경함. (886)

SET @exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'reminder_schedule'
    AND index_name = 'idx_reminder_schedule_member'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_reminder_schedule_member ON reminder_schedule(member_id)',
  'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE reminder_schedule
DROP INDEX uq_member_schedule;

ALTER TABLE reminder_schedule
    ADD COLUMN active_key BIGINT
        GENERATED ALWAYS AS (CASE WHEN deleted_at IS NULL THEN 0 ELSE id END) STORED;

CREATE UNIQUE INDEX uq_member_schedule_active
    ON reminder_schedule(member_id, schedule, active_key);
