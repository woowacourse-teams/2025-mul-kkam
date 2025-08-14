CREATE TABLE cup_imoji
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    uri     VARCHAR(255)    NOT NULL
);

ALTER TABLE cup
    ADD COLUMN cup_imoji_id BIGINT;

ALTER TABLE cup
    ADD CONSTRAINT fk_cup_imoji
        FOREIGN KEY (cup_imoji_id) REFERENCES cup_imoji (id);

ALTER TABLE cup DROP COLUMN emoji;
