-- Agregar columna email
ALTER TABLE users ADD COLUMN email VARCHAR(255) NOT NULL UNIQUE;

-- Agregar columna full_name
ALTER TABLE users ADD COLUMN full_name VARCHAR(255) NOT NULL;

-- Actualizar usuarios existentes con valores por defecto
UPDATE users SET email = CONCAT(username, '@example.com'), full_name = username WHERE email IS NULL; 