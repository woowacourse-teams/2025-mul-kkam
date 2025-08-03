CREATE TABLE target_amount_snapshot
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,

    member_id     BIGINT    NOT NULL,
    updated_at    TIMESTAMP NOT NULL,
    target_amount INT       NOT NULL,

    CONSTRAINT fk_target_amount_snapshot_member FOREIGN KEY (member_id) REFERENCES member (id)
);
