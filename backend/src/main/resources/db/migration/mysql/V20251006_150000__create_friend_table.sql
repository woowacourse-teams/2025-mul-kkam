CREATE TABLE friend_request (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                requester_id BIGINT NOT NULL,
                                addressee_id BIGINT NOT NULL,

                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                deleted_at DATETIME NULL,

                                user_low  BIGINT AS (LEAST(requester_id, addressee_id)) STORED,
                                user_high BIGINT AS (GREATEST(requester_id, addressee_id)) STORED,

                                active_key BOOLEAN AS (CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END) STORED,

                                CONSTRAINT fk_fr_req_requester FOREIGN KEY (requester_id) REFERENCES member(id),
                                CONSTRAINT fk_fr_req_addressee FOREIGN KEY (addressee_id) REFERENCES member(id),

                                CONSTRAINT uq_friend_request_active UNIQUE (user_low, user_high, active_key),

                                INDEX idx_fr_req_addressee (addressee_id, created_at DESC),
                                INDEX idx_fr_req_requester (requester_id, created_at DESC)
);

CREATE TABLE friend (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        requester_id BIGINT NOT NULL,
                        addressee_id BIGINT NOT NULL,

                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        deleted_at DATETIME NULL,

                        user_low  BIGINT AS (LEAST(requester_id, addressee_id)) STORED,
                        user_high BIGINT AS (GREATEST(requester_id, addressee_id)) STORED,

                        active_key BOOLEAN AS (CASE WHEN deleted_at IS NULL THEN TRUE ELSE NULL END) STORED,

                        CONSTRAINT fk_friend_requester FOREIGN KEY (requester_id) REFERENCES member(id),
                        CONSTRAINT fk_friend_addressee FOREIGN KEY (addressee_id) REFERENCES member(id),

                        CONSTRAINT uq_friend_active UNIQUE (user_low, user_high, active_key)
);
