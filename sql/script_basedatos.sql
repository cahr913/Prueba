-- ============================================================
-- Script de base de datos - ProyectoGestionLab (LabControl)
-- Sistema de gestion de laboratorios educativos
-- Motor recomendado: MySQL 8.x o MariaDB
-- Uso:    mysql -u root -p < script_basedatos.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS gestion_laboratorio
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_laboratorio;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS bitacora_acceso;
DROP TABLE IF EXISTS instalacion;
DROP TABLE IF EXISTS computadora;
DROP TABLE IF EXISTS mantenimiento;
DROP TABLE IF EXISTS equipo;
DROP TABLE IF EXISTS software;
DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS laboratorio;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS centro_educativo;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Centro educativo
CREATE TABLE centro_educativo (
    id INT NOT NULL AUTO_INCREMENT,
    codigo_institucional VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(30),
    correo VARCHAR(100),
    director VARCHAR(100),
    logo_path VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. Usuario (heredan logica: Administrador, Docente, Tecnico, Encargado)
CREATE TABLE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    correo VARCHAR(100),
    rol ENUM('Administrador','Docente','Tecnico','Encargado de laboratorio') NOT NULL DEFAULT 'Docente',
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion DATE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 3. Laboratorio
CREATE TABLE laboratorio (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    ubicacion VARCHAR(200),
    capacidad INT,
    estado VARCHAR(30),
    responsable VARCHAR(100),
    centro_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (centro_id) REFERENCES centro_educativo(id)
) ENGINE=InnoDB;

-- 4. Equipo (superclase de Computadora)
CREATE TABLE equipo (
    id INT NOT NULL AUTO_INCREMENT,
    codigo_inventario VARCHAR(50) NOT NULL UNIQUE,
    tipo VARCHAR(50),
    num_serie VARCHAR(100),
    marca VARCHAR(100),
    modelo VARCHAR(100),
    fecha_adquisicion DATE,
    estado VARCHAR(30),
    laboratorio_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id)
) ENGINE=InnoDB;

-- 5. Computadora (subclase de Equipo - herencia por tabla compartida)
CREATE TABLE computadora (
    id INT NOT NULL,
    hostname VARCHAR(100),
    ip VARCHAR(45),
    sistema_operativo VARCHAR(100),
    arquitectura VARCHAR(30),
    caracteristicas TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES equipo(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Software
CREATE TABLE software (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    version VARCHAR(50),
    tipo VARCHAR(50),
    licencia VARCHAR(100),
    estado VARCHAR(30),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 7. Instalacion (relacion muchos a muchos Equipo <-> Software)
CREATE TABLE instalacion (
    id INT NOT NULL AUTO_INCREMENT,
    equipo_id INT NOT NULL,
    software_id INT NOT NULL,
    fecha_instalacion DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id) ON DELETE CASCADE,
    FOREIGN KEY (software_id) REFERENCES software(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Horario
CREATE TABLE horario (
    id INT NOT NULL AUTO_INCREMENT,
    docente_id INT,
    laboratorio_id INT,
    curso VARCHAR(150),
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    PRIMARY KEY (id),
    FOREIGN KEY (docente_id) REFERENCES usuario(id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id)
) ENGINE=InnoDB;

-- 9. Mantenimiento (Preventivo / Correctivo)
CREATE TABLE mantenimiento (
    id INT NOT NULL AUTO_INCREMENT,
    equipo_id INT,
    tipo ENUM('Preventivo','Correctivo') NOT NULL,
    fecha DATE,
    descripcion TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id)
) ENGINE=InnoDB;

-- 10. Bitacora de acceso
CREATE TABLE bitacora_acceso (
    id INT NOT NULL AUTO_INCREMENT,
    fecha_hora DATETIME NOT NULL,
    accion VARCHAR(100),
    ip VARCHAR(45),
    dispositivo VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Indices utiles para consultas frecuentes
CREATE INDEX idx_lab_centro        ON laboratorio(centro_id);
CREATE INDEX idx_equipo_lab        ON equipo(laboratorio_id);
CREATE INDEX idx_horario_lab       ON horario(laboratorio_id);
CREATE INDEX idx_horario_docente   ON horario(docente_id);
CREATE INDEX idx_instalacion_equipo ON instalacion(equipo_id);
CREATE INDEX idx_instalacion_software ON instalacion(software_id);
CREATE INDEX idx_mant_equipo       ON mantenimiento(equipo_id);

-- ============================================================
-- Datos de ejemplo (opcional, borrar si no se necesitan)
-- ============================================================
INSERT INTO centro_educativo (codigo_institucional, nombre, direccion, telefono, correo, director) VALUES
('CE-001', 'Instituto Tecnologico Central', '6a Avenida 5-20, Zona 1', '2230-1122', 'central@itc.edu', 'Ing. Ana Perez'),
('CE-002', 'Colegio San Lucas', '12 Calle 8-45, Zona 10', '2368-4455', 'sanlucas@colegio.edu', 'Lic. Jorge Mejia');

INSERT INTO usuario (nombre, apellido, nombre_usuario, contrasena, correo, rol, estado, fecha_creacion) VALUES
('Administrador', 'Sistema', 'admin', 'admin123', 'admin@lab.edu', 'Administrador', 1, CURDATE()),
('Maria', 'Rodriguez', 'mrodriguez', 'clave123', 'mrodriguez@lab.edu', 'Docente', 1, CURDATE());

INSERT INTO laboratorio (codigo, nombre, ubicacion, capacidad, estado, responsable, centro_id) VALUES
('LAB-001', 'Laboratorio de Computo A', 'Edificio A, segundo nivel', 30, 'Activo', 'Maria Rodriguez', 1),
('LAB-002', 'Laboratorio de Quimica', 'Edificio B, primer nivel', 20, 'Activo', NULL, 1);

SELECT 'Base de datos gestion_lab creada correctamente' AS mensaje;
