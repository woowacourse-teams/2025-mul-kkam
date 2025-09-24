ALTER TABLE target_amount_snapshot
    MODIFY COLUMN updated_at DATE NOT NULL DEFAULT (CURRENT_DATE);
