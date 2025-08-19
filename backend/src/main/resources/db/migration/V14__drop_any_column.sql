ALTER TABLE intake_history_detail
    ADD COLUMN test datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE intake_history_detail
    DROP COLUMN test;
