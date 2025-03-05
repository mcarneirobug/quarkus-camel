-- quarkus automatically execute this script when the application starts in dev mode.
CREATE TABLE IF NOT EXISTS failed_messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    message_type VARCHAR(255),
    payload CLOB,
    status VARCHAR(50),
    error_message CLOB,
    retry_count INT DEFAULT 0
);