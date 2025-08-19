-- ALLOW_DROP
-- reason: rename 과정에서 기존 제약/컬럼 drop 필요 (PR-1234)
ALTER TABLE member
    DROP COLUMN nickname;
ALTER TABLE member
    ADD COLUMN nickname VARCHAR(10) NOT NULL;

ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(50) NULL UNIQUE;
