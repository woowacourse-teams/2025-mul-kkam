CREATE TABLE outbox_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    type VARCHAR(50) NOT NULL,
    member_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body VARCHAR(1000) NOT NULL,

    status VARCHAR(20) NOT NULL,

    dedupe_key VARCHAR(255) NOT NULL UNIQUE,

    attempt_count INT NOT NULL DEFAULT 0,

    last_error VARCHAR(255),

    next_attempt_at TIMESTAMP NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE outbox_notification
    ADD CONSTRAINT chk_outbox_status
        CHECK (status IN ('READY', 'SENDING', 'SENT', 'RETRY', 'FAIL'));

CREATE INDEX idx_outbox_status_nextAttempt
    ON outbox_notification (status, next_attempt_at);

CREATE INDEX idx_outbox_memberId
    ON outbox_notification (member_id);

CREATE INDEX idx_outbox_token
    ON outbox_notification (token);
