-- ALLOW_DROP
-- reason: reminder_schedule에는 soft delete 예정이므로 유니크키를 삭제함. 코드로 처리할 예정 (884)

ALTER TABLE reminder_schedule
DROP INDEX uq_member_schedule;

ALTER TABLE reminder_schedule
    ADD UNIQUE KEY uq_member_schedule_active (
    member_id,
    schedule,
    (deleted_at IS NULL)
    );

ALTER TABLE reminder_schedule
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE reminder_schedule
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;
