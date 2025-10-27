-- ALLOW_DROP
-- reason: reminder_schedule에는 soft delete 예정이므로 유니크키를 변경함. (886)

ALTER TABLE reminder_schedule
DROP CONSTRAINT IF EXISTS uq_member_schedule;

ALTER TABLE reminder_schedule
    ADD COLUMN active_key BOOLEAN
    AS (CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END);

CREATE UNIQUE INDEX uq_member_schedule_active
    ON reminder_schedule(member_id, schedule, active_key);
