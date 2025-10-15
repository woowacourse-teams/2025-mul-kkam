-- ALLOW_DROP
-- reason: 친구 관계 하나의 엔티티로 관리. (914)

DROP TABLE IF EXISTS friend_request;
DROP TABLE IF EXISTS friend;

CREATE TABLE friend_relation (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id  BIGINT NOT NULL,
    addressee_id  BIGINT NOT NULL,

    friend_status ENUM('REQUEST','ACCEPTED') NOT NULL,

    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    DATETIME NULL,

    user_low      BIGINT AS (LEAST(requester_id, addressee_id)) STORED,
    user_high     BIGINT AS (GREATEST(requester_id, addressee_id)) STORED,

    active_key    TINYINT AS (CASE WHEN deleted_at IS NULL THEN 1 ELSE NULL END) STORED,

    CONSTRAINT fk_fr_rel_requester FOREIGN KEY (requester_id) REFERENCES member(id),
    CONSTRAINT fk_fr_rel_addressee FOREIGN KEY (addressee_id) REFERENCES member(id),

    UNIQUE KEY uq_fr_rel_pair_active (user_low, user_high, active_key)
);
