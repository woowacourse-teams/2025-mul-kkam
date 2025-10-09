-- ===========================
-- friend_request (변경 없음)
-- ===========================
CREATE TABLE friend_request
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT    NOT NULL,
    addressee_id BIGINT    NOT NULL,

    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP NULL,

    user_low     BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN requester_id ELSE addressee_id END
        ),
    user_high    BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN addressee_id ELSE requester_id END
        ),

    active_key   BOOLEAN GENERATED ALWAYS AS (
        CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END
        ),

    CONSTRAINT fk_fr_req_requester FOREIGN KEY (requester_id) REFERENCES member (id),
    CONSTRAINT fk_fr_req_addressee FOREIGN KEY (addressee_id) REFERENCES member (id),
    CONSTRAINT uq_friend_request_active UNIQUE (user_low, user_high, active_key)
);

CREATE INDEX idx_fr_req_addressee ON friend_request (addressee_id, created_at);
CREATE INDEX idx_fr_req_requester ON friend_request (requester_id, created_at);

-- ===========================
-- friend (responded_at 추가)
-- ===========================
CREATE TABLE friend
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT    NOT NULL,
    addressee_id BIGINT    NOT NULL,

    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP NULL,
    responded_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    user_low     BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN requester_id ELSE addressee_id END
        ),
    user_high    BIGINT GENERATED ALWAYS AS (
        CASE WHEN requester_id < addressee_id THEN addressee_id ELSE requester_id END
        ),

    active_key   BOOLEAN GENERATED ALWAYS AS (
        CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END
        ),

    CONSTRAINT fk_friend_requester FOREIGN KEY (requester_id) REFERENCES member (id),
    CONSTRAINT fk_friend_addressee FOREIGN KEY (addressee_id) REFERENCES member (id),
    CONSTRAINT uq_friend_active UNIQUE (user_low, user_high, active_key)
);