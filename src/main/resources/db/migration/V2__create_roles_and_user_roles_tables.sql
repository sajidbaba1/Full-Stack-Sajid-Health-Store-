-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default roles if they don't exist
INSERT IGNORE INTO roles (name) VALUES ('USER');
INSERT IGNORE INTO roles (name) VALUES ('ADMIN');
INSERT IGNORE INTO roles (name) VALUES ('SELLER');

-- Update existing users to have USER role by default
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u
CROSS JOIN roles r 
WHERE r.name = 'USER' 
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id);
