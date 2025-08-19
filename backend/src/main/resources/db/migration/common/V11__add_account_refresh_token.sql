CREATE TABLE account_refresh_token(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id      BIGINT  NOT NULL,
    refresh_token   VARCHAR(255) NOT NULL,

    CONSTRAINT fk_oauth_account_id
        FOREIGN KEY (account_id) REFERENCES oauth_account (id)
);
