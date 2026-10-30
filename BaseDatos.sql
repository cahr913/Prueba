CREATE DATABASE IF NOT EXISTS gestion_laboratorio
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_laboratorio;

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

CREATE TABLE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    correo VARCHAR(100),
    rol ENUM('Administrador','Docente','Responsable de laboratorio') NOT NULL DEFAULT 'Docente',
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion DATE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE laboratorio (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    ubicacion VARCHAR(200),
    capacidad INT,
    estado VARCHAR(30) DEFAULT 'Activo',
    responsable VARCHAR(100),
    observaciones TEXT,
    centro_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (centro_id) REFERENCES centro_educativo(id)
) ENGINE=InnoDB;

CREATE TABLE equipo (
    id INT NOT NULL AUTO_INCREMENT,
    codigo_inventario VARCHAR(50) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    num_serie VARCHAR(100),
    marca VARCHAR(100),
    modelo VARCHAR(100),
    fecha_adquisicion DATE,
    estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
    observaciones TEXT,
    laboratorio_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id)
) ENGINE=InnoDB;

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

CREATE TABLE software (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    version VARCHAR(50),
    tipo ENUM('Sistema','Aplicacion') NOT NULL DEFAULT 'Aplicacion',
    tipo_licencia VARCHAR(100),
    estado VARCHAR(30) DEFAULT 'Activo',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE instalacion (
    id INT NOT NULL AUTO_INCREMENT,
    equipo_id INT NOT NULL,
    software_id INT NOT NULL,
    fecha_instalacion DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id) ON DELETE CASCADE,
    FOREIGN KEY (software_id) REFERENCES software(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE horario (
    id INT NOT NULL AUTO_INCREMENT,
    docente_id INT NOT NULL,
    laboratorio_id INT NOT NULL,
    curso VARCHAR(150),
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (docente_id) REFERENCES usuario(id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id)
) ENGINE=InnoDB;

CREATE TABLE recepcion (
    id INT NOT NULL AUTO_INCREMENT,
    laboratorio_id INT NOT NULL,
    docente_id INT NOT NULL,
    horario_id INT NULL,
    fecha_hora DATETIME NOT NULL,
    estado_general VARCHAR(100),
    limpieza VARCHAR(100),
    estado_mobiliario VARCHAR(100),
    energia_electrica TINYINT(1) DEFAULT 1,
    red TINYINT(1) DEFAULT 1,
    estado_equipos VARCHAR(100),
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id),
    FOREIGN KEY (docente_id) REFERENCES usuario(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id)
) ENGINE=InnoDB;

CREATE TABLE recepcion_equipo_dano (
    id INT NOT NULL AUTO_INCREMENT,
    recepcion_id INT NOT NULL,
    equipo_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (recepcion_id) REFERENCES recepcion(id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipo(id)
) ENGINE=InnoDB;

CREATE TABLE reporte_dano (
    id INT NOT NULL AUTO_INCREMENT,
    laboratorio_id INT NOT NULL,
    equipo_id INT NOT NULL,
    docente_id INT NOT NULL,
    horario_id INT NULL,
    tipo_falla VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_hora_reporte DATETIME NOT NULL,
    estado_encontrado VARCHAR(50) NOT NULL,
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (laboratorio_id) REFERENCES laboratorio(id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id),
    FOREIGN KEY (docente_id) REFERENCES usuario(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id)
) ENGINE=InnoDB;

CREATE TABLE foto_evidencia (
    id INT NOT NULL AUTO_INCREMENT,
    reporte_dano_id INT NOT NULL,
    equipo_id INT NULL,
    ruta_archivo VARCHAR(255) NOT NULL,
    descripcion VARCHAR(200),
    PRIMARY KEY (id),
    FOREIGN KEY (reporte_dano_id) REFERENCES reporte_dano(id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipo(id)
) ENGINE=InnoDB;

CREATE TABLE mantenimiento (
    id INT NOT NULL AUTO_INCREMENT,
    numero_servicio VARCHAR(30) NOT NULL UNIQUE,
    equipo_id INT NOT NULL,
    reporte_dano_id INT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    responsable_id INT NOT NULL,
    tipo ENUM('Preventivo','Correctivo') NOT NULL,
    estado ENUM('PENDIENTE','EN_PROCESO','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    falla_reportada VARCHAR(200),
    diagnostico TEXT,
    actividades_realizadas TEXT,
    resultado VARCHAR(100),
    estado_final_equipo VARCHAR(50),
    recomendaciones TEXT,
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (equipo_id) REFERENCES equipo(id),
    FOREIGN KEY (reporte_dano_id) REFERENCES reporte_dano(id),
    FOREIGN KEY (responsable_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE pieza (
    id INT NOT NULL AUTO_INCREMENT,
    mantenimiento_id INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    cantidad INT NOT NULL DEFAULT 1,
    accion ENUM('REPARACION','REEMPLAZO') NOT NULL,
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (mantenimiento_id) REFERENCES mantenimiento(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE bitacora_acceso (
    id INT NOT NULL AUTO_INCREMENT,
    fecha_hora DATETIME NOT NULL,
    accion VARCHAR(100),
    ip VARCHAR(45),
    dispositivo VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

