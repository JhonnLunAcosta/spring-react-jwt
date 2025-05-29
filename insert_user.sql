-- La contraseña 'admin123' está encriptada con BCrypt
INSERT INTO users (username, password, role) 
VALUES ('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'ROLE_ADMIN'); 