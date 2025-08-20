CREATE TABLE oauth_account
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,

    member_id      BIGINT,

    CONSTRAINT fk_oauth_account_member
        FOREIGN KEY (member_id) REFERENCES member (id),

    oauth_id       VARCHAR(255) NOT NULL,

    oauth_provider VARCHAR(255) NOT NULL
);
