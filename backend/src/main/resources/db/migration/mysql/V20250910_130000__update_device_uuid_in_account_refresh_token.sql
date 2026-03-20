UPDATE account_refresh_token AS art
    JOIN device d ON d.id = art.device_uuid
    SET art.device_uuid = d.device_uuid
WHERE art.device_uuid IS NULL
  AND d.member_id = (SELECT aoa.member_id
    FROM oauth_account aoa
    WHERE aoa.id = art.account_id);
