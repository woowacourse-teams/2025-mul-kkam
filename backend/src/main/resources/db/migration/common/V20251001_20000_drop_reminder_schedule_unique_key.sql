-- ALLOW_DROP
-- reason: reminder_schedule에는 soft delete 예정이므로 유니크키를 삭제함. 코드로 처리할 예정 (884)

ALTER TABLE reminder_schedule
DROP INDEX uq_member_schedule;
