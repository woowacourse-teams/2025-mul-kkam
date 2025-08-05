ALTER TABLE intake_history
    ADD COLUMN date DATE NOT NULL,
    ADD COLUMN target_amount INT NOT NULL;

CREATE TABLE intake_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    intake_history_id BIGINT NOT NULL,
    time TIME NOT NULL,
    intake_amount INT NOT NULL,
    CONSTRAINT fk_intake_detail_intake_history FOREIGN KEY (intake_history_id) REFERENCES intake_history(id)
);
