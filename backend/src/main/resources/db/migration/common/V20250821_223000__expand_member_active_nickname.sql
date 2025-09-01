ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(10) NULL;

CREATE UNIQUE INDEX uq_member_active_nickname ON member (active_nickname);
