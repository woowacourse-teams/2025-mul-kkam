DELETE
FROM account_refresh_token
WHERE device_uuid IS NULL;

ALTER TABLE account_refresh_token
    ADD CONSTRAINT uk_account_device_uuid
        UNIQUE (account_id, device_uuid);


