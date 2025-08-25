-- ALLOW_DROP
-- reason: title 필드를 content로 수정했는데 컬럼 삭제를 안함
ALTER TABLE notification
    DROP COLUMN title;

