-- ALLOW_DROP
-- reason: nickname 컬럼에 걸린 고유 인덱스명을 동적으로 조회해 드롭 (679)

ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(10);

CREATE UNIQUE INDEX uq_member_active_nickname ON member (active_nickname);
