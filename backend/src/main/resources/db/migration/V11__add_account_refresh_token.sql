CREATE TABLE account_refresh_token(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id      BIGINT  NOT NULL,
    refresh_token   VARCHAR(255) NOT NULL
);
