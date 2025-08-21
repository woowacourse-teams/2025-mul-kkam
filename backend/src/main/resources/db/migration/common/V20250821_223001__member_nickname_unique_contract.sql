-- V20250821_223001__member_nickname_unique_contract.sql
-- nickname 컬럼에 걸린 고유 인덱스명을 동적으로 조회해 드롭
SET @idx := (
    SELECT index_name
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
    AND table_name   = 'member'
    AND column_name  = 'nickname'
    AND non_unique   = 0
    AND index_name  <> 'PRIMARY'
    LIMIT 1
);

SET @sql := IF(@idx IS NOT NULL,
    CONCAT('ALTER TABLE member DROP INDEX `', @idx, '`'),
    'SELECT 1');  -- 없으면 noop

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
