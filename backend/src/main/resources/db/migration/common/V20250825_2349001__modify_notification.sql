-- ALLOW_DROP
-- reason: 추천 음수량은 suggestion notification 으로 분리함에 따라 notification 에서 삭제 (679)
ALTER TABLE notification
    DROP COLUMN recommended_target_amount;
