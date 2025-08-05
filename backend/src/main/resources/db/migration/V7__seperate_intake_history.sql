ALTER TABLE intake_history
    ADD COLUMN history_date DATE NOT NULL;

ALTER TABLE intake_history
    ADD COLUMN streak INT NOT NULL;

ALTER TABLE intake_history
    DROP COLUMN date_time;

ALTER TABLE intake_history
    DROP COLUMN intake_amount;

CREATE TABLE intake_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    intake_history_id BIGINT NOT NULL,
    intake_time TIME NOT NULL,
    intake_amount INT NOT NULL,
    CONSTRAINT fk_intake_detail_intake_history FOREIGN KEY (intake_history_id) REFERENCES intake_history(id)
);
