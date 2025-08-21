-- V20250821_222700__member_active_nickname_expand.sql
-- active_nickname 도입 및 legacy nickname과 공존/동기화

ALTER TABLE member
    ADD COLUMN active_nickname VARCHAR(10)
        GENERATED ALWAYS AS (
            CASE WHEN deleted_at IS NULL THEN nickname ELSE NULL END
        );

CREATE UNIQUE INDEX uq_member_active_nickname ON member (active_nickname);
