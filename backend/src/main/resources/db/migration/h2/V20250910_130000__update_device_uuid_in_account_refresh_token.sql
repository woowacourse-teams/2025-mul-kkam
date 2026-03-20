UPDATE account_refresh_token art
SET device_uuid = (SELECT d.device_uuid
                   FROM device d
                   WHERE d.id = art.device_uuid
                     AND d.member_id = (SELECT aoa.member_id
                                        FROM oauth_account aoa
                                        WHERE aoa.id = art.account_id))
WHERE device_uuid IS NULL;
