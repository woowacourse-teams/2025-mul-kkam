DROP INDEX IF EXISTS nickname ON member;

ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(50);
