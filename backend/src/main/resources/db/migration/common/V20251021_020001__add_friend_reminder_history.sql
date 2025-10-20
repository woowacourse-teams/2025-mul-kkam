CREATE TABLE friend_reminder_history (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id    BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    quota_date   DATE   NOT NULL,
    remaining    SMALLINT NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   DATETIME NULL,
    CONSTRAINT uk_sender_recipient_quota_date UNIQUE (sender_id, recipient_id, quota_date),

    -- FK로 직접 참조 X, 조회 성능을 위한 단일 컬럼 인덱스만 추가
    INDEX idx_friend_reminder_history_sender_id (sender_id),
    INDEX idx_friend_reminder_history_recipient_id (recipient_id)
);
