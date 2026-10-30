USE gestion_laboratorio;

-- centro educativo
INSERT INTO centro_educativo (codigo_institucional, nombre, direccion, telefono, correo, director, logo_path) VALUES
('CE-001', 'Instituto Escuela', 'Final 5ta calle, zona 2', '2222-1201', 'info@ite.edu.gt', 'Ing. Ana Perez', NULL);

-- laboratorios (centro_id = 1)
INSERT INTO laboratorio (codigo, nombre, ubicacion, capacidad, estado, responsable, observaciones, centro_id) VALUES
('LAB-001', 'Laboratorio de Computo A', 'Edificio A, segundo nivel', 30, 'Activo', 'Tecnico', NULL, 1),
('LAB-002', 'Laboratorio de Computo B', 'Edificio B, primer nivel', 24, 'Activo', 'Tecnico', NULL, 1);

-- equipos (laboratorio_id: 1=LAB-001, 2=LAB-002)
INSERT INTO equipo (codigo_inventario, tipo, num_serie, marca, modelo, fecha_adquisicion, estado, observaciones, laboratorio_id) VALUES
('EQ-001', 'Computadora', 'SN-HP-0001', 'HP', 'ProDesk 400 G7', '2023-02-15', 'ACTIVO', NULL, 1),
('EQ-002', 'Computadora', 'SN-HP-0002', 'HP', 'ProDesk 400 G7', '2023-02-15', 'ACTIVO', NULL, 1),
('EQ-003', 'Computadora', 'SN-DELL-0103', 'Dell', 'OptiPlex 3090', '2022-08-10', 'ACTIVO', NULL, 1),
('EQ-004', 'Computadora', 'SN-DELL-0104', 'Dell', 'OptiPlex 3090', '2022-08-10', 'DAÑADO', 'No enciende, reportada por docente', 1),
('EQ-005', 'Impresora', 'SN-EP-7700', 'Epson', 'L5290', '2021-05-20', 'ACTIVO', NULL, 1),
('EQ-006', 'Switch', 'SN-TP-24P', 'TP-Link', 'TL-SG1024', '2021-05-20', 'ACTIVO', NULL, 1),
('EQ-007', 'Computadora', 'SN-LE-2001', 'Lenovo', 'ThinkCentre M70q', '2024-01-30', 'ACTIVO', NULL, 2),
('EQ-008', 'Proyector', 'SN-BENQ-99', 'BenQ', 'MX535', '2020-03-12', 'EN MANTENIMIENTO', 'En revision de lampara', 2);


-- computadoras (id = equipo.id)
INSERT INTO computadora (id, hostname, ip, sistema_operativo, arquitectura, caracteristicas) VALUES
(1, 'PC-LAB001-01', '192.168.10.11', 'Windows 11 Pro', 'x64', 'Core i5, 8 GB RAM, 256 GB SSD'),
(2, 'PC-LAB001-02', '192.168.10.12', 'Windows 11 Pro', 'x64', 'Core i5, 8 GB RAM, 256 GB SSD'),
(3, 'PC-LAB001-03', '192.168.10.13', 'Ubuntu 24.04 LTS', 'x64', 'Ryzen 5, 16 GB RAM, 512 GB SSD'),
(4, 'PC-LAB001-04', '192.168.10.14', 'Windows 10 Pro', 'x64', 'Core i3, 4 GB RAM, 500 GB HDD'),
(7, 'PC-LAB002-01', '192.168.20.11', 'Windows 11 Pro', 'x64', 'Core i5, 16 GB RAM, 512 GB SSD');

-- software
INSERT INTO software (nombre, version, tipo, tipo_licencia, estado) VALUES
('Windows 11 Pro', '23H2', 'Sistema', 'Comercial', 'Activo'),
('Ubuntu', '24.04 LTS', 'Sistema', 'Open Source', 'Activo'),
('LibreOffice', '24.2', 'Aplicacion', 'Open Source', 'Activo'),
('NetBeans IDE', '21', 'Aplicacion', 'Open Source', 'Activo'),
('AutoCAD', '2024', 'Aplicacion', 'Comercial', 'Activo');

-- instalaciones (equipo_id, software_id)
INSERT INTO instalacion (equipo_id, software_id, fecha_instalacion) VALUES
(1, 1, '2023-03-01'),
(1, 3, '2023-03-01'),
(2, 1, '2023-03-01'),
(3, 2, '2023-09-01'),
(3, 4, '2023-09-05'),
(7, 1, '2024-02-10'),
(7, 5, '2024-02-15');

-- horarios (docente_id = 2, laboratorio_id = 1 o 2)
INSERT INTO horario (docente_id, laboratorio_id, curso, fecha, hora_inicio, hora_fin, observaciones) VALUES
(2, 1, 'Programacion II', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '08:00:00', '10:00:00', 'Seccion A'),
(2, 1, 'Programacion II - Laboratorio', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00:00', '12:00:00', 'Seccion A'),
(2, 2, 'Base de Datos I', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '14:00:00', '16:00:00', NULL),
(2, 1, 'Base de Datos I - Practica', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00:00', '11:00:00', NULL);

-- recepcion (laboratorio_id = 1, docente_id = 2)
INSERT INTO recepcion (laboratorio_id, docente_id, horario_id, fecha_hora, estado_general, limpieza, estado_mobiliario, energia_electrica, red, estado_equipos, observaciones) VALUES
(1, 2, 1, NOW() - INTERVAL 5 DAY, 'Regular', 'Buena', 'Bueno', 1, 1, 'Un equipo dañado detectado', 'Se encontro una computadora sin encender');

INSERT INTO recepcion_equipo_dano (recepcion_id, equipo_id) VALUES
(1, 4);

-- reporte de daño (equipo_id = 4, docente_id = 2)
INSERT INTO reporte_dano (laboratorio_id, equipo_id, docente_id, horario_id, tipo_falla, descripcion, fecha_hora_reporte, estado_encontrado, observaciones) VALUES
(1, 4, 2, 1, 'No enciende', 'La computadora EQ-004 no enciende al presionar el boton de encendido, se escucha un clic pero no arranca.', NOW() - INTERVAL 5 DAY, 'DAÑADO', 'Se encontro durante la recepcion del laboratorio');



-- mantenimiento (responsable_id = 3 = tecnico)
INSERT INTO mantenimiento (numero_servicio, equipo_id, reporte_dano_id, fecha_inicio, fecha_fin, responsable_id, tipo, estado, falla_reportada, diagnostico, actividades_realizadas, resultado, estado_final_equipo, recomendaciones, observaciones) VALUES
('MS-0001', 4, 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, 'Correctivo', 'FINALIZADO', 'No enciende', 'Fuente de poder quemada por sobrecarga electrica', 'Revision de fuente interna, reemplazo de fuente de poder y pruebas de encendido por 2 horas', 'Equipo reparado', 'ACTIVO', 'Instalar regulador de voltaje en el puesto', 'Se recomienda revision electrica del circuito del laboratorio');

INSERT INTO pieza (mantenimiento_id, nombre, descripcion, cantidad, accion, observaciones) VALUES
(1, 'Fuente de poder 300W', 'Fuente ATX generica 300 watts', 1, 'REEMPLAZO', 'Pieza nueva instalada'),
(1, 'Cable de alimentacion', 'Cable de poder de 6 pies', 1, 'REPARACION', 'Se resoldo el conector');

INSERT INTO mantenimiento (numero_servicio, equipo_id, reporte_dano_id, fecha_inicio, fecha_fin, responsable_id, tipo, estado, falla_reportada, diagnostico, actividades_realizadas, resultado, estado_final_equipo, recomendaciones, observaciones) VALUES
('MS-0002', 5, NULL, DATE_ADD(CURDATE(), INTERVAL 7 DAY), NULL, 3, 'Preventivo', 'PENDIENTE', NULL, NULL, 'Limpieza de cabezales y revision de rodillos', NULL, NULL, 'Realizar limpieza interna cada 6 meses', NULL);
