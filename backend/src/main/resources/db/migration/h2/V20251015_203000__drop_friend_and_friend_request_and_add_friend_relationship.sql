-- ALLOW_DROP
-- reason: 친구 관계 하나의 엔티티로 관리. (914)

DROP TABLE IF EXISTS friend_request;
DROP TABLE IF EXISTS friend;

CREATE TABLE friend_relation (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id  BIGINT NOT NULL,
    addressee_id  BIGINT NOT NULL,

    friend_relation_status        VARCHAR(16) NOT NULL
    CHECK (friend_relation_status IN ('REQUESTED','ACCEPTED')),

    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP NULL,

    user_low      BIGINT AS (CASE WHEN requester_id < addressee_id THEN requester_id ELSE addressee_id END),
    user_high     BIGINT AS (CASE WHEN requester_id < addressee_id THEN addressee_id ELSE requester_id END),

    active_key    BOOLEAN AS (CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END),

    FOREIGN KEY (requester_id) REFERENCES member(id),
    FOREIGN KEY (addressee_id) REFERENCES member(id)
);

CREATE UNIQUE INDEX uq_fr_rel_pair_active ON friend_relation(user_low, user_high, active_key);
