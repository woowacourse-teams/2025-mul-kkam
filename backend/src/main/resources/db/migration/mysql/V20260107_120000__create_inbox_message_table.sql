-- Inbox 테이블 생성 (Consumer Worker용)
CREATE TABLE inbox_message (
    message_id VARCHAR(36) PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    error_message TEXT,
    
    INDEX idx_inbox_created_at (created_at),
    INDEX idx_inbox_status (status)
);
