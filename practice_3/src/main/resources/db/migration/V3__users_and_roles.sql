CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role)
);

INSERT INTO app_user (username, password_hash, enabled)
VALUES ('student', '{bcrypt}$2a$10$36fiBT2zOEr/KtKN95bVZOWZRm1g3pNOuMWAolK49V8bCJ7mlKCfG', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO app_user (username, password_hash, enabled)
VALUES ('admin', '{bcrypt}$2a$10$8.Mg3ZgPEzXR/D/uCyEdvu4LuXPFlSBZShhw5wtnGyLbFF/rIqRb2', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_role (user_id, role)
SELECT id, 'STUDENT' FROM app_user WHERE username = 'student'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_role (user_id, role)
SELECT id, 'ADMIN' FROM app_user WHERE username = 'admin'
ON CONFLICT (user_id, role) DO NOTHING;
