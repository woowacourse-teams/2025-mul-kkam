-- ALLOW_DROP
-- reason: oauth_account 는 soft delete 대상 아님 (843)
ALTER TABLE oauth_account
    DROP COLUMN deleted_at;
