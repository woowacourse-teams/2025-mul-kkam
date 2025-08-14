CREATE TABLE cup_emoji
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    url     VARCHAR(255)    NOT NULL
);

ALTER TABLE cup
    ADD COLUMN cup_emoji_id BIGINT;

ALTER TABLE cup
    ADD CONSTRAINT fk_cup_emoji
        FOREIGN KEY (cup_emoji_id) REFERENCES cup_emoji (id);

ALTER TABLE cup DROP COLUMN emoji;
