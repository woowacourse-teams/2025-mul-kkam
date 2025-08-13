ALTER TABLE member
    ADD COLUMN is_marketing_notification_agreed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE member
    ADD COLUMN is_night_notification_agreed BOOLEAN NOT NULL DEFAULT FALSE;
