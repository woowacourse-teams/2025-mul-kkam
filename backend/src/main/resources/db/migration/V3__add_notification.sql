CREATE TABLE notification
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,

    intake_type        VARCHAR(50)  NOT NULL,
    notification_type  VARCHAR(50),

    title              VARCHAR(255) NOT NULL,
    is_read            BOOLEAN      NOT NULL,
    created_at         TIMESTAMP    NOT NULL,

    recommended_amount INT          NOT NULL
);
