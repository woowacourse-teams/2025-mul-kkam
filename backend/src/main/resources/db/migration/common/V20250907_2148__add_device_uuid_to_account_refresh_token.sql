ALTER TABLE device
    MODIFY COLUMN `device_id` VARCHAR(50) NULL;

ALTER TABLE device
    ADD COLUMN `device_uuid` VARCHAR(255);

UPDATE device
SET device_uuid = device_id
WHERE device_uuid IS NULL;

ALTER TABLE
    device MODIFY COLUMN device_uuid VARCHAR(255) NOT NULL;

ALTER TABLE account_refresh_token
    ADD COLUMN device_uuid VARCHAR(255);
