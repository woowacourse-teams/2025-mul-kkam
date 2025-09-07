UPDATE account_refresh_token art
SET device_uuid = (SELECT d.deviceUuid
                   FROM device d
                            JOIN oauth_account doa ON doa.id = d.oauth_account_id
                   WHERE d.id = art.device_id
                     AND doa.member_id = (SELECT aoa.member_id FROM oauth_account aoa WHERE aoa.id = art.account_id))
WHERE device_uuid IS NULL;
