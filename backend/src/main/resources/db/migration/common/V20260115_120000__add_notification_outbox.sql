CREATE TABLE notification_outbox
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,

    target_type        VARCHAR(20)  NOT NULL,
    status             VARCHAR(20)  NOT NULL,

    title              VARCHAR(255) NOT NULL,
    body               VARCHAR(255) NOT NULL,
    action             VARCHAR(50)  NOT NULL,

    topic              VARCHAR(255),
    token              VARCHAR(255),

    attempt_count      INT          NOT NULL,
    max_attempts       INT          NOT NULL,
    next_attempt_at    TIMESTAMP    NOT NULL,
    sent_at            TIMESTAMP,

    last_error_code    VARCHAR(100),
    last_error_message TEXT,

    created_at         TIMESTAMP    NOT NULL,
    deleted_at         TIMESTAMP
);

CREATE INDEX idx_notification_outbox_status_next_attempt_at
    ON notification_outbox (status, next_attempt_at);
