ALTER TABLE notification
    RENAME COLUMN recommended_amount TO recommended_target_amount;

ALTER TABLE notification
    ADD COLUMN member_id BIGINT NOT NULL;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_member
        FOREIGN KEY (member_id) REFERENCES member(id);
