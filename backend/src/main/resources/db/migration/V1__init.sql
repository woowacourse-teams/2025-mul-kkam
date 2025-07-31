CREATE TABLE member
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    nickname      VARCHAR(10) NOT NULL UNIQUE,
    gender        VARCHAR(255),
    weight        DOUBLE,
    target_amount INT         NOT NULL
);

CREATE TABLE cup
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT      NOT NULL,
    nickname   VARCHAR(10) NOT NULL,
    cup_amount INT         NOT NULL,
    cup_rank   INT         NOT NULL,
    CONSTRAINT fk_cup_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE intake_history
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id     BIGINT    NOT NULL,
    date_time     TIMESTAMP NOT NULL,
    intake_amount INT       NOT NULL,
    target_amount INT       NOT NULL,
    CONSTRAINT fk_intake_history_member FOREIGN KEY (member_id) REFERENCES member (id)
);
