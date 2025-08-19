-- ALLOW_DROP
-- reason: rename 과정에서 기존 제약/컬럼 drop 필요 (PR-1234)
DROP INDEX CONSTRAINT_87 ON member;
ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(50) NULL UNIQUE;
