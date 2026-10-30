package com.labcontrol.dao;

import com.labcontrol.db.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDao {

    
    public static class Filtros {
        public int laboratorioId;
        public int equipoId;
        public int docenteId;
        public int responsableId;
        public String estadoEquipo;
        public String tipoMantenimiento;
        public String accionPieza;
        public LocalDate desde;
        public LocalDate hasta;

        public boolean tieneFechas() {
            return desde != null || hasta != null;
        }
    }

    private final StringBuilder sql = new StringBuilder();
    private final List<Object> parametros = new ArrayList<>();

    
    public ResultadoReporte inventarioPorLaboratorio(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT l.nombre AS laboratorio, e.codigo_inventario, e.tipo, e.num_serie, e.marca, e.modelo, e.estado ")
           .append("FROM equipo e JOIN laboratorio l ON l.id = e.laboratorio_id WHERE 1=1");
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.estadoEquipo != null, " AND e.estado = ?", f.estadoEquipo);
        si(f.equipoId > 0, " AND e.id = ?", f.equipoId);
        sql.append(" ORDER BY l.nombre, e.codigo_inventario");

        ResultadoReporte r = new ResultadoReporte("Inventario de equipos por laboratorio",
                new String[]{"Laboratorio", "Código inventario", "Tipo", "Serie", "Marca", "Modelo", "Estado"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte equiposPorEstado(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT e.estado, l.nombre AS laboratorio, COUNT(*) AS cantidad ")
           .append("FROM equipo e JOIN laboratorio l ON l.id = e.laboratorio_id WHERE 1=1");
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.estadoEquipo != null, " AND e.estado = ?", f.estadoEquipo);
        sql.append(" GROUP BY e.estado, l.nombre ORDER BY e.estado, l.nombre");

        ResultadoReporte r = new ResultadoReporte("Equipos clasificados por estado",
                new String[]{"Estado", "Laboratorio", "Cantidad de equipos"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte historialMantenimiento(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT e.codigo_inventario, m.numero_servicio, m.tipo, m.estado, m.fecha_inicio, m.fecha_fin, ")
           .append("m.falla_reportada, m.resultado, CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS responsable ")
           .append("FROM mantenimiento m ")
           .append("JOIN equipo e ON e.id = m.equipo_id ")
           .append("JOIN usuario u ON u.id = m.responsable_id WHERE 1=1");
        si(f.equipoId > 0, " AND m.equipo_id = ?", f.equipoId);
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.responsableId > 0, " AND m.responsable_id = ?", f.responsableId);
        si(f.tipoMantenimiento != null, " AND m.tipo = ?", f.tipoMantenimiento);
        si(f.desde != null, " AND m.fecha_inicio >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND m.fecha_inicio <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" ORDER BY e.codigo_inventario, m.fecha_inicio DESC");

        ResultadoReporte r = new ResultadoReporte("Historial de mantenimiento por equipo",
                new String[]{"Equipo", "Nº servicio", "Tipo", "Estado", "Inicio", "Fin",
                        "Falla reportada", "Resultado", "Responsable"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte cantidadMantenimientos(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT e.codigo_inventario, e.tipo, l.nombre AS laboratorio, ")
           .append("COUNT(m.id) AS total, ")
           .append("SUM(m.tipo = 'Correctivo') AS correctivos, ")
           .append("SUM(m.tipo = 'Preventivo') AS preventivos ")
           .append("FROM equipo e ")
           .append("JOIN laboratorio l ON l.id = e.laboratorio_id ")
           .append("LEFT JOIN mantenimiento m ON m.equipo_id = e.id WHERE 1=1");
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.equipoId > 0, " AND e.id = ?", f.equipoId);
        si(f.desde != null, " AND m.fecha_inicio >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND m.fecha_inicio <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" GROUP BY e.id, e.codigo_inventario, e.tipo, l.nombre ")
           .append("ORDER BY total DESC, e.codigo_inventario");

        ResultadoReporte r = new ResultadoReporte("Cantidad de mantenimientos por equipo",
                new String[]{"Equipo", "Tipo", "Laboratorio", "Total mantenimientos",
                        "Correctivos", "Preventivos"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte mantenimientosPorTipo(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT m.tipo, m.estado, COUNT(*) AS cantidad ")
           .append("FROM mantenimiento m JOIN equipo e ON e.id = m.equipo_id WHERE 1=1");
        si(f.tipoMantenimiento != null, " AND m.tipo = ?", f.tipoMantenimiento);
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.responsableId > 0, " AND m.responsable_id = ?", f.responsableId);
        si(f.desde != null, " AND m.fecha_inicio >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND m.fecha_inicio <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" GROUP BY m.tipo, m.estado ORDER BY m.tipo, m.estado");

        ResultadoReporte r = new ResultadoReporte("Mantenimientos preventivos y correctivos",
                new String[]{"Tipo de mantenimiento", "Estado del servicio", "Cantidad"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte mantenimientosPorResponsable(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS responsable, u.rol, ")
           .append("COUNT(m.id) AS total, ")
           .append("SUM(m.tipo = 'Correctivo') AS correctivos, ")
           .append("SUM(m.tipo = 'Preventivo') AS preventivos ")
           .append("FROM mantenimiento m JOIN usuario u ON u.id = m.responsable_id ")
           .append("JOIN equipo e ON e.id = m.equipo_id WHERE 1=1");
        si(f.responsableId > 0, " AND m.responsable_id = ?", f.responsableId);
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.desde != null, " AND m.fecha_inicio >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND m.fecha_inicio <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" GROUP BY u.id, u.nombre, u.apellido, u.rol ORDER BY total DESC");

        ResultadoReporte r = new ResultadoReporte("Mantenimientos realizados por responsable",
                new String[]{"Responsable", "Rol", "Total", "Correctivos", "Preventivos"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte equiposConMasFallas(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT e.codigo_inventario, e.tipo, l.nombre AS laboratorio, ")
           .append("COUNT(rd.id) AS reportes_dano, ")
           .append("(SELECT COUNT(*) FROM mantenimiento m2 WHERE m2.equipo_id = e.id) AS mantenimientos ")
           .append("FROM equipo e ")
           .append("JOIN laboratorio l ON l.id = e.laboratorio_id ")
           .append("LEFT JOIN reporte_dano rd ON rd.equipo_id = e.id WHERE 1=1");
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.equipoId > 0, " AND e.id = ?", f.equipoId);
        sql.append(" GROUP BY e.id, e.codigo_inventario, e.tipo, l.nombre ")
           .append("ORDER BY reportes_dano DESC, mantenimientos DESC LIMIT 15");

        ResultadoReporte r = new ResultadoReporte("Equipos con mayor cantidad de fallas o mantenimientos",
                new String[]{"Equipo", "Tipo", "Laboratorio", "Reportes de daño", "Mantenimientos"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte piezasReparadas(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT p.nombre, p.accion, p.cantidad, e.codigo_inventario, m.numero_servicio, ")
           .append("m.fecha_inicio, CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS responsable ")
           .append("FROM pieza p ")
           .append("JOIN mantenimiento m ON m.id = p.mantenimiento_id ")
           .append("JOIN equipo e ON e.id = m.equipo_id ")
           .append("JOIN usuario u ON u.id = m.responsable_id WHERE 1=1");
        si(f.accionPieza != null, " AND p.accion = ?", f.accionPieza);
        si(f.laboratorioId > 0, " AND e.laboratorio_id = ?", f.laboratorioId);
        si(f.equipoId > 0, " AND e.id = ?", f.equipoId);
        si(f.responsableId > 0, " AND m.responsable_id = ?", f.responsableId);
        si(f.desde != null, " AND m.fecha_inicio >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND m.fecha_inicio <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" ORDER BY m.fecha_inicio DESC, p.id");

        ResultadoReporte r = new ResultadoReporte("Piezas reparadas o reemplazadas",
                new String[]{"Pieza", "Acción", "Cantidad", "Equipo", "Nº servicio",
                        "Fecha", "Responsable"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte recepcionesPorDocente(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT r.fecha_hora, l.nombre AS laboratorio, ")
           .append("CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS docente, ")
           .append("r.estado_general, r.limpieza, r.estado_mobiliario, ")
           .append("r.energia_electrica, r.red, r.estado_equipos, r.observaciones ")
           .append("FROM recepcion r ")
           .append("JOIN laboratorio l ON l.id = r.laboratorio_id ")
           .append("JOIN usuario u ON u.id = r.docente_id WHERE 1=1");
        si(f.docenteId > 0, " AND r.docente_id = ?", f.docenteId);
        si(f.laboratorioId > 0, " AND r.laboratorio_id = ?", f.laboratorioId);
        si(f.desde != null, " AND DATE(r.fecha_hora) >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND DATE(r.fecha_hora) <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" ORDER BY r.fecha_hora DESC");

        ResultadoReporte r = new ResultadoReporte("Recepciones de laboratorio registradas por docentes",
                new String[]{"Fecha y hora", "Laboratorio", "Docente", "Estado general",
                        "Limpieza", "Mobiliario", "Energía", "Red", "Estado equipos", "Observaciones"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte equiposDanadosPorLaboratorio(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT l.nombre AS laboratorio, e.codigo_inventario, e.tipo, rd.tipo_falla, ")
           .append("rd.fecha_hora_reporte, CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS docente, ")
           .append("rd.descripcion ")
           .append("FROM reporte_dano rd ")
           .append("JOIN laboratorio l ON l.id = rd.laboratorio_id ")
           .append("JOIN equipo e ON e.id = rd.equipo_id ")
           .append("JOIN usuario u ON u.id = rd.docente_id WHERE 1=1");
        si(f.laboratorioId > 0, " AND rd.laboratorio_id = ?", f.laboratorioId);
        si(f.docenteId > 0, " AND rd.docente_id = ?", f.docenteId);
        si(f.equipoId > 0, " AND rd.equipo_id = ?", f.equipoId);
        si(f.desde != null, " AND DATE(rd.fecha_hora_reporte) >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND DATE(rd.fecha_hora_reporte) <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" ORDER BY l.nombre, rd.fecha_hora_reporte DESC");

        ResultadoReporte r = new ResultadoReporte("Equipos dañados encontrados por laboratorio",
                new String[]{"Laboratorio", "Equipo", "Tipo", "Falla reportada",
                        "Fecha del reporte", "Docente", "Descripción"});
        ejecutar(r);
        return r;
    }

    
    public ResultadoReporte programacionLaboratorios(Filtros f) throws SQLException {
        sql.setLength(0);
        parametros.clear();
        sql.append("SELECT h.fecha, l.nombre AS laboratorio, h.curso, ")
           .append("CONCAT(u.nombre, ' ', IFNULL(u.apellido, '')) AS docente, ")
           .append("h.hora_inicio, h.hora_fin, h.observaciones ")
           .append("FROM horario h ")
           .append("JOIN laboratorio l ON l.id = h.laboratorio_id ")
           .append("JOIN usuario u ON u.id = h.docente_id WHERE 1=1");
        si(f.laboratorioId > 0, " AND h.laboratorio_id = ?", f.laboratorioId);
        si(f.docenteId > 0, " AND h.docente_id = ?", f.docenteId);
        si(f.desde != null, " AND h.fecha >= ?", java.sql.Date.valueOf(f.desde));
        si(f.hasta != null, " AND h.fecha <= ?", java.sql.Date.valueOf(f.hasta));
        sql.append(" ORDER BY h.fecha, h.hora_inicio");

        ResultadoReporte r = new ResultadoReporte("Programación de uso de los laboratorios",
                new String[]{"Fecha", "Laboratorio", "Curso / actividad", "Docente",
                        "Hora inicio", "Hora fin", "Observaciones"});
        ejecutar(r);
        return r;
    }


    
    private void si(boolean condicion, String fragmento, Object valor) {
        if (condicion) {
            sql.append(fragmento);
            parametros.add(valor);
        }
    }

    
    private void ejecutar(ResultadoReporte resultado) throws SQLException {
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                int columnas = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    resultado.agregarFila(fila);
                }
            }
        }
    }
}
