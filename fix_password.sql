-- Actualizar la contraseña del usuario admin con un hash conocido y probado
UPDATE users 
SET password = '$2a$10$SB4j9VinD79Zl8NOrZaKQ.8pu5LO.oJseUV4b0pmU2tFdEjh/30ka'
WHERE username = 'admin'; 