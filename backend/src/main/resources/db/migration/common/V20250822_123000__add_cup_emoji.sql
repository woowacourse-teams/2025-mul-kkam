CREATE TABLE cup_emoji
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP    NULL
);

ALTER TABLE cup
    ADD COLUMN cup_emoji_id BIGINT;

ALTER TABLE cup
    ADD CONSTRAINT fk_cup_emoji
        FOREIGN KEY (cup_emoji_id) REFERENCES cup_emoji (id);

INSERT INTO cup_emoji (url, created_at, deleted_at)
VALUES ('https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c', '2025-08-01 10:10:10', NULL),
       ('https://github.com/user-attachments/assets/393fc8f9-bc46-4856-bfbe-889efc97151e', '2025-08-01 10:10:11', NULL);

-- ALLOW_DROP
-- reason: cup과 cupEmoji 테이블 분리 과정에서 기존 제약/컬럼 drop 필요 (PR-519)
ALTER TABLE cup
    DROP COLUMN emoji;

ALTER TABLE intake_history_detail
    ADD COLUMN cup_emoji_url VARCHAR(255);
