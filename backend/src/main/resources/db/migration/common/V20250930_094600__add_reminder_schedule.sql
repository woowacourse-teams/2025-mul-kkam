CREATE TABLE reminder_schedule(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    schedule  TIME   NOT NULL,

    CONSTRAINT fk_reminder_schedule_member
        FOREIGN KEY (member_id) REFERENCES member (id),

    CONSTRAINT uq_member_schedule UNIQUE (member_id, schedule)
);