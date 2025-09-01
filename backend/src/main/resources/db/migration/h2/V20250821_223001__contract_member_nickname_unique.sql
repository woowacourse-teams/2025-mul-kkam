-- ALLOW_DROP
-- reason: nickname 컬럼에 걸린 고유 인덱스명을 동적으로 조회해 드롭 (679)

DROP INDEX IF EXISTS PUBLIC.UQ_MEMBER_NICKNAME;
