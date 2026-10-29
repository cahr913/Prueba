-- ============================================================
-- Usuarios de prueba para validar roles y pantallas
-- Ejecutar DESPUÉS de BaseDatos.sql
-- ============================================================
USE gestion_lab;

-- 1. Administrador (puede gestionar todo: usuarios, laboratorios, equipos, reportes)
INSERT INTO usuario (nombre, apellido, nombre_usuario, contrasena, correo, rol, estado, fecha_creacion) VALUES
('Cristian Alejandro', 'Huertas Robledo', 'admin', 'admin123', 'admin@itc.edu', 'Administrador', 1, CURDATE());

-- 2. Docente / Profesor (consulta sus horarios y registra recepción del laboratorio)
INSERT INTO usuario (nombre, apellido, nombre_usuario, contrasena, correo, rol, estado, fecha_creacion) VALUES
('María', 'Rodríguez', 'mrodriguez', 'clave123', 'mrodriguez@itc.edu', 'Docente', 1, CURDATE());

-- 3. Técnico / Responsable de laboratorio (consulta daños y registra mantenimientos)
INSERT INTO usuario (nombre, apellido, nombre_usuario, contrasena, correo, rol, estado, fecha_creacion) VALUES
('Carlos', 'Méndez', 'cmendez', 'clave123', 'cmendez@itc.edu', 'Responsable de laboratorio', 1, CURDATE());

SELECT 'Usuarios de prueba creados correctamente' AS mensaje;
SELECT nombre_usuario, rol FROM usuario ORDER BY id;
