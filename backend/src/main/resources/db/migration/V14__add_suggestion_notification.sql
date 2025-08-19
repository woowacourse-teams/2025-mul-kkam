ALTER TABLE notification
DROP COLUMN recommended_target_amount;

CREATE TABLE suggestion_notification
(
    id                              BIGINT PRIMARY KEY,
    recommended_target_amount       INT     NOT NULL,
    apply_target_amount             BOOLEAN NOT NULL,
    CONSTRAINT fk_suggestion_notification_notification FOREIGN KEY (id) REFERENCES notification (id)
);
