-- 1) cup → member
ALTER TABLE cup
    DROP FOREIGN KEY fk_cup_member;
ALTER TABLE cup
    ADD CONSTRAINT fk_cup_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 2) intake_history → member
ALTER TABLE intake_history
    DROP FOREIGN KEY fk_intake_history_member;
ALTER TABLE intake_history
    ADD CONSTRAINT fk_intake_history_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 3) intake_history_detail → intake_history
ALTER TABLE intake_history_detail
    DROP FOREIGN KEY fk_intake_history_detail_intake_history;
ALTER TABLE intake_history_detail
    ADD CONSTRAINT fk_intake_history_detail_intake_history
        FOREIGN KEY (intake_history_id) REFERENCES intake_history (id) ON DELETE CASCADE;

-- 4) target_amount_snapshot → member
ALTER TABLE target_amount_snapshot
    DROP FOREIGN KEY fk_target_amount_snapshot_member;
ALTER TABLE target_amount_snapshot
    ADD CONSTRAINT fk_target_amount_snapshot_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 5) oauth_account → member
ALTER TABLE oauth_account
    DROP FOREIGN KEY fk_oauth_account_member;
ALTER TABLE oauth_account
    ADD CONSTRAINT fk_oauth_account_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 6) account_refresh_token → oauth_account
ALTER TABLE account_refresh_token
    DROP FOREIGN KEY fk_oauth_account_id;
ALTER TABLE account_refresh_token
    ADD CONSTRAINT fk_oauth_account_id
        FOREIGN KEY (account_id) REFERENCES oauth_account (id) ON DELETE CASCADE;

-- 7) device → member
ALTER TABLE device
    DROP FOREIGN KEY fk_device_member_id;
ALTER TABLE device
    ADD CONSTRAINT fk_device_member_id
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 8) notification → member
ALTER TABLE notification
    DROP FOREIGN KEY fk_notification_member;
ALTER TABLE notification
    ADD CONSTRAINT fk_notification_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;
