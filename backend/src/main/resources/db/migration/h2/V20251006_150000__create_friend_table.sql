CREATE TABLE friend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    addressee_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',

    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    responded_at TIMESTAMP NULL,
    deleted_at  TIMESTAMP NULL,

    user_low  BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN requester_id ELSE addressee_id END
        ),
    user_high BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN addressee_id ELSE requester_id END
        ),

    -- 활성 행만 유니크 적용
    active_key TINYINT GENERATED ALWAYS AS (
        CASE WHEN deleted_at IS NULL THEN 1 ELSE NULL END
        ),

    CONSTRAINT fk_friend_requester FOREIGN KEY (requester_id) REFERENCES member(id),
    CONSTRAINT fk_friend_addressee FOREIGN KEY (addressee_id) REFERENCES member(id),

    CONSTRAINT uq_friend_pair_active UNIQUE (user_low, user_high, active_key),

    INDEX idx_addressee_status (addressee_id, status),
    INDEX idx_requester_status (requester_id, status)
);
