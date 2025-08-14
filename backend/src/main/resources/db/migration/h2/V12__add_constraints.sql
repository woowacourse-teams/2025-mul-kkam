ALTER TABLE cup
    DROP CONSTRAINT IF EXISTS fk_cup_member;
ALTER TABLE cup
    ADD CONSTRAINT fk_cup_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

ALTER TABLE intake_history
    DROP CONSTRAINT IF EXISTS fk_intake_history_member;
ALTER TABLE intake_history
    ADD CONSTRAINT fk_intake_history_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

ALTER TABLE intake_history_detail
    DROP CONSTRAINT IF EXISTS fk_intake_history_detail_intake_history;
ALTER TABLE intake_history_detail
    ADD CONSTRAINT fk_intake_history_detail_intake_history
        FOREIGN KEY (intake_history_id) REFERENCES intake_history (id) ON DELETE CASCADE;

ALTER TABLE target_amount_snapshot
    DROP CONSTRAINT IF EXISTS fk_target_amount_snapshot_member;
ALTER TABLE target_amount_snapshot
    ADD CONSTRAINT fk_target_amount_snapshot_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

ALTER TABLE oauth_account
    DROP CONSTRAINT IF EXISTS fk_oauth_account_member;
ALTER TABLE oauth_account
    ADD CONSTRAINT fk_oauth_account_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

ALTER TABLE account_refresh_token
    DROP CONSTRAINT IF EXISTS fk_oauth_account_id;
ALTER TABLE account_refresh_token
    ADD CONSTRAINT fk_oauth_account_id
        FOREIGN KEY (account_id) REFERENCES oauth_account (id) ON DELETE CASCADE;

ALTER TABLE device
    DROP CONSTRAINT IF EXISTS fk_device_member_id;
ALTER TABLE device
    ADD CONSTRAINT fk_device_member_id
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

ALTER TABLE notification
    DROP CONSTRAINT IF EXISTS fk_notification_member;
ALTER TABLE notification
    ADD CONSTRAINT fk_notification_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;
