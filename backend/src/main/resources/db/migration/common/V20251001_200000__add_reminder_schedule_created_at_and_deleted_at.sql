ALTER TABLE reminder_schedule
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE reminder_schedule
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;
