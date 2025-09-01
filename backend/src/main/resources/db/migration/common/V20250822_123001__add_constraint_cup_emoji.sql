ALTER TABLE cup
    ADD COLUMN cup_emoji_id BIGINT DEFAULT 1;

ALTER TABLE cup
    ADD CONSTRAINT fk_cup_emoji
        FOREIGN KEY (cup_emoji_id) REFERENCES cup_emoji (id);
