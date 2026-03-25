ALTER TABLE member
    ADD COLUMN member_role VARCHAR(20) NULL;

UPDATE member
SET member_role = 'MEMBER'
WHERE member_role IS NULL;

ALTER TABLE member
    MODIFY COLUMN member_role VARCHAR(20) NOT NULL;

ALTER TABLE member
    ALTER COLUMN member_role SET DEFAULT 'NONE';
