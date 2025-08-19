ALTER TABLE member
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE member
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE notification
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE cup
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE cup
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE oauth_account
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE oauth_account
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE intake_history
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE intake_history
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE intake_history_detail
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE intake_history_detail
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE device
    ADD COLUMN deleted_at datetime NULL;

ALTER TABLE device
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE target_amount_snapshot
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE target_amount_snapshot
    ADD COLUMN deleted_at datetime NULL;

