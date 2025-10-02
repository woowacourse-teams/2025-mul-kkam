ALTER TABLE reminder_schedule
    ADD COLUMN active_idx TINYINT(1)
    GENERATED ALWAYS AS (CASE WHEN deleted_at IS NULL THEN 1 ELSE NULL END) STORED;

CREATE UNIQUE INDEX uq_member_schedule_active
    ON reminder_schedule(member_id, schedule, active_idx);
