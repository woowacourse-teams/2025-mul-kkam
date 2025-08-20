ALTER TABLE intake_history_detail
    ADD COLUMN cup_id BIGINT NOT NULL;;

ALTER TABLE intake_history_detail
    ADD CONSTRAINT fk_intake_history_detail_cup
        FOREIGN KEY (cup_id) REFERENCES cup(id);
