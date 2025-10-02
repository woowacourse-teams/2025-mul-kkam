ALTER TABLE reminder_schedule
    ADD COLUMN active_idx BOOLEAN
    AS (CASE WHEN deleted_at IS NULL THEN TRUE ELSE FALSE END);

CREATE UNIQUE INDEX uq_member_schedule_active
    ON reminder_schedule(member_id, schedule, active_idx);
