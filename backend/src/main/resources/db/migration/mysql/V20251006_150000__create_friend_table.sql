CREATE TABLE friend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    addressee_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',

    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at DATETIME NULL,
    deleted_at  DATETIME NULL,

    -- 중복 친구 요청 방지를 위한 생성 칼럼 (A -> B 신청 시, B -> A 신청 못하도록)
    user_low  BIGINT AS (LEAST(requester_id, addressee_id)) STORED,
    user_high BIGINT AS (GREATEST(requester_id, addressee_id)) STORED,

    -- 활성 행만 유니크 적용 (삭제되면 NULL이라 유니크 제외)
    active_key TINYINT AS (CASE WHEN deleted_at IS NULL THEN 1 ELSE NULL END) STORED,

    CONSTRAINT fk_friend_requester FOREIGN KEY (requester_id) REFERENCES member(id),
    CONSTRAINT fk_friend_addressee FOREIGN KEY (addressee_id) REFERENCES member(id),

    CONSTRAINT uq_friend_pair_active UNIQUE (user_low, user_high, active_key),

    INDEX idx_addressee_status (addressee_id, status),
    INDEX idx_requester_status (requester_id, status)
);
