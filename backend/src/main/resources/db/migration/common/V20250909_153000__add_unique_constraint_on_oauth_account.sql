ALTER TABLE oauth_account
    ADD CONSTRAINT uq_oauth_account UNIQUE (oauth_id, oauth_provider);
