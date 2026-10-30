USE gestion_laboratorio;

-- borrar toda la base de datos
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS pieza;
DROP TABLE IF EXISTS mantenimiento;
DROP TABLE IF EXISTS foto_evidencia;
DROP TABLE IF EXISTS reporte_dano;
DROP TABLE IF EXISTS recepcion_equipo_dano;
DROP TABLE IF EXISTS recepcion;
DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS instalacion;
DROP TABLE IF EXISTS computadora;
DROP TABLE IF EXISTS equipo;
DROP TABLE IF EXISTS software;
DROP TABLE IF EXISTS laboratorio;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS centro_educativo;
DROP TABLE IF EXISTS bitacora_acceso;

SET FOREIGN_KEY_CHECKS = 1;