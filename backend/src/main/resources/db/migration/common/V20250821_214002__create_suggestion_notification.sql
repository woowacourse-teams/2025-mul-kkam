CREATE TABLE suggestion_notification
(
    id                        BIGINT PRIMARY KEY,
    recommended_target_amount INT     NOT NULL,
    apply_target_amount       BOOLEAN NOT NULL,
    CONSTRAINT fk_suggestion_notification_notification FOREIGN KEY (id) REFERENCES notification (id)
);

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

ALTER TABLE suggestion_notification
    ADD COLUMN created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE suggestion_notification
    ADD COLUMN deleted_at datetime NULL;
