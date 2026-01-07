-- Inbox 테이블 생성 (Consumer Worker용)
CREATE TABLE inbox_message (
    message_id VARCHAR(36) PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    error_message CLOB
);

CREATE INDEX idx_inbox_created_at ON inbox_message(created_at);
CREATE INDEX idx_inbox_status ON inbox_message(status);
