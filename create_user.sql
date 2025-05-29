-- Eliminar usuario si existe
DELETE FROM users WHERE username = 'admin';

-- Insertar nuevo usuario con contraseña encriptada en BCrypt
-- La contraseña es 'admin123'
INSERT INTO users (username, password, role) 
VALUES ('admin', '$2a$10$PrI5Gk9L.tYZNXm6xPKDoOPEPdUw3GSG1j8yHVF2kn2ScC6zpQ4Ru', 'ROLE_ADMIN'); 