package com.labcontrol.dao;

import com.labcontrol.modelo.Mantenimiento;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDao {

    public void insertar(Mantenimiento m) throws SQLException {
        String sql = "INSERT INTO mantenimiento(numero_servicio, equipo_id, reporte_dano_id, fecha_inicio, fecha_fin, responsable_id, tipo, falla_reportada, diagnostico, actividades_realizadas, resultado, estado_final_equipo, recomendaciones, observaciones) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNumeroServicio());
            ps.setInt(2, m.getEquipoId());
            ps.setInt(3, m.getReporteDanoId());
            ps.setDate(4, m.getFechaInicio() != null ? java.sql.Date.valueOf(m.getFechaInicio()) : null);
            ps.setDate(5, m.getFechaFin() != null ? java.sql.Date.valueOf(m.getFechaFin()) : null);
            ps.setInt(6, m.getResponsableId());
            ps.setString(7, m.getTipoTexto());
            ps.setString(8, m.getFallaReportada());
            ps.setString(9, m.getDiagnostico());
            ps.setString(10, m.getActividadesRealizadas());
            ps.setString(11, m.getResultado());
            ps.setString(12, m.getEstadoFinalEquipo());
            ps.setString(13, m.getRecomendaciones());
            ps.setString(14, m.getObservaciones());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Mantenimiento m) throws SQLException {
        String sql = "UPDATE mantenimiento SET numero_servicio=?, equipo_id=?, reporte_dano_id=?, fecha_inicio=?, fecha_fin=?, responsable_id=?, tipo=?, falla_reportada=?, diagnostico=?, actividades_realizadas=?, resultado=?, estado_final_equipo=?, recomendaciones=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getNumeroServicio());
            ps.setInt(2, m.getEquipoId());
            ps.setInt(3, m.getReporteDanoId());
            ps.setDate(4, m.getFechaInicio() != null ? java.sql.Date.valueOf(m.getFechaInicio()) : null);
            ps.setDate(5, m.getFechaFin() != null ? java.sql.Date.valueOf(m.getFechaFin()) : null);
            ps.setInt(6, m.getResponsableId());
            ps.setString(7, m.getTipoTexto());
            ps.setString(8, m.getFallaReportada());
            ps.setString(9, m.getDiagnostico());
            ps.setString(10, m.getActividadesRealizadas());
            ps.setString(11, m.getResultado());
            ps.setString(12, m.getEstadoFinalEquipo());
            ps.setString(13, m.getRecomendaciones());
            ps.setString(14, m.getObservaciones());
            ps.setInt(15, m.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM mantenimiento WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Mantenimiento buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM mantenimiento WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mantenimiento m = new Mantenimiento();
                    m.setId(rs.getInt("id"));
                    m.setNumeroServicio(rs.getString("numero_servicio"));
                    m.setEquipoId(rs.getInt("equipo_id"));
                    m.setReporteDanoId(rs.getInt("reporte_dano_id"));
                    java.sql.Date fi = rs.getDate("fecha_inicio");
                    m.setFechaInicio(fi != null ? fi.toLocalDate() : null);
                    java.sql.Date ff = rs.getDate("fecha_fin");
                    m.setFechaFin(ff != null ? ff.toLocalDate() : null);
                    m.setResponsableId(rs.getInt("responsable_id"));
                    m.setTipoTexto(rs.getString("tipo"));
                    m.setFallaReportada(rs.getString("falla_reportada"));
                    m.setDiagnostico(rs.getString("diagnostico"));
                    m.setActividadesRealizadas(rs.getString("actividades_realizadas"));
                    m.setResultado(rs.getString("resultado"));
                    m.setEstadoFinalEquipo(rs.getString("estado_final_equipo"));
                    m.setRecomendaciones(rs.getString("recomendaciones"));
                    m.setObservaciones(rs.getString("observaciones"));
                    return m;
                }
            }
        }
        return null;
    }

    public List<Mantenimiento> listar() throws SQLException {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento ORDER BY fecha_inicio DESC";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Mantenimiento m = new Mantenimiento();
                m.setId(rs.getInt("id"));
                m.setNumeroServicio(rs.getString("numero_servicio"));
                m.setEquipoId(rs.getInt("equipo_id"));
                m.setReporteDanoId(rs.getInt("reporte_dano_id"));
                java.sql.Date fi = rs.getDate("fecha_inicio");
                m.setFechaInicio(fi != null ? fi.toLocalDate() : null);
                java.sql.Date ff = rs.getDate("fecha_fin");
                m.setFechaFin(ff != null ? ff.toLocalDate() : null);
                m.setResponsableId(rs.getInt("responsable_id"));
                m.setTipoTexto(rs.getString("tipo"));
                m.setFallaReportada(rs.getString("falla_reportada"));
                m.setDiagnostico(rs.getString("diagnostico"));
                m.setActividadesRealizadas(rs.getString("actividades_realizadas"));
                m.setResultado(rs.getString("resultado"));
                m.setEstadoFinalEquipo(rs.getString("estado_final_equipo"));
                m.setRecomendaciones(rs.getString("recomendaciones"));
                m.setObservaciones(rs.getString("observaciones"));
                lista.add(m);
            }
        }
        return lista;
    }
}
