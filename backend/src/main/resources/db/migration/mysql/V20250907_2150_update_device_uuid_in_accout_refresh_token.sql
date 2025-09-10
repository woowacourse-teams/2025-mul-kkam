UPDATE account_refresh_token AS art
    JOIN device d          ON d.id = art.device_id
    JOIN oauth_account doa ON doa.id = d.oauth_account_id
    JOIN oauth_account aoa ON aoa.id = art.account_id
    SET art.device_uuid = d.device_uuid
WHERE art.device_uuid IS NULL
  AND doa.member_id = aoa.member_id;
