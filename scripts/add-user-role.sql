ALTER TABLE users ADD COLUMN role varchar(20);
UPDATE users SET role = 'USER' WHERE role IS NULL;
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'USER';

-- Promote an existing account manually when needed:
-- UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
