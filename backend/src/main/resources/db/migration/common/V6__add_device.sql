CREATE TABLE device
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    token   VARCHAR(255) NOT NULL,
    device_id VARCHAR(50) NOT NULL,
    member_id  BIGINT      NOT NULL,
    CONSTRAINT fk_device_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);
