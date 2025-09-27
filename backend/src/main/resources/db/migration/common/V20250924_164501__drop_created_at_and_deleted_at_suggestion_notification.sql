-- ALLOW_DROP
-- reason: suggestion_notification 은 soft delete 대상 아님. notification 을 통해 soft delete 이뤄짐 (843)

ALTER TABLE suggestion_notification
    DROP COLUMN created_at;

ALTER TABLE suggestion_notification
    DROP COLUMN deleted_at;
