package com.labcontrol.dao;

import com.labcontrol.modelo.ReporteDano;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class ReporteDanoDao {

    public void insertar(ReporteDano r) throws SQLException {
        if (r.getLaboratorioid() <= 0 && r.getEquipoid() > 0) {
            String sqlLab = "SELECT laboratorio_id FROM equipo WHERE id = (?)";
            try (Connection cn = ConexionBD.abrir();
                 PreparedStatement ps = cn.prepareStatement(sqlLab)) {
                ps.setInt(1, r.getEquipoid());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        r.setLaboratorioid(rs.getInt("laboratorio_id"));
                    }
                }
            }
        }

        String sql = "INSERT INTO reporte_dano(laboratorio_id, equipo_id, docente_id, horario_id, "
                + "tipo_falla, descripcion, fecha_hora_reporte, estado_encontrado, observaciones) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getLaboratorioid());
            ps.setInt(2, r.getEquipoid());
            ps.setInt(3, r.getDocenteid());
            if (r.getHorarioid() > 0) {
                ps.setInt(4, r.getHorarioid());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, r.getTipoFalla());
            ps.setString(6, r.getDescripcion());
            ps.setTimestamp(7, r.getFechaHora() != null ? Timestamp.valueOf(r.getFechaHora()) : null);
            ps.setString(8, r.getEstadoEncontrado());
            ps.setString(9, r.getObservaciones());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(ReporteDano r) throws SQLException {
        String sql = "UPDATE reporte_dano SET laboratorio_id=?, equipo_id=?, docente_id=?, horario_id=?, tipo_falla=?, descripcion=?, fecha_hora_reporte=?, estado_encontrado=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, r.getLaboratorioid());
            ps.setInt(2, r.getEquipoid());
            ps.setInt(3, r.getDocenteid());
            if (r.getHorarioid() > 0) {
                ps.setInt(4, r.getHorarioid());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, r.getTipoFalla());
            ps.setString(6, r.getDescripcion());
            ps.setTimestamp(7, r.getFechaHora() != null ? Timestamp.valueOf(r.getFechaHora()) : null);
            ps.setString(8, r.getEstadoEncontrado());
            ps.setString(9, r.getObservaciones());
            ps.setInt(10, r.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM reporte_dano WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public ReporteDano buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM reporte_dano WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReporteDano r = new ReporteDano();
                    r.setId(rs.getInt("id"));
                    r.setLaboratorioid(rs.getInt("laboratorio_id"));
                    r.setEquipoid(rs.getInt("equipo_id"));
                    r.setDocenteid(rs.getInt("docente_id"));
                    r.setHorarioid(rs.getInt("horario_id"));
                    r.setTipoFalla(rs.getString("tipo_falla"));
                    r.setDescripcion(rs.getString("descripcion"));
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_hora_reporte");
                    r.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                    r.setEstadoEncontrado(rs.getString("estado_encontrado"));
                    r.setObservaciones(rs.getString("observaciones"));
                    return r;
                }
            }
        }
        return null;
    }

    public List<ReporteDano> listar() throws SQLException {
        List<ReporteDano> lista = new ArrayList<>();
        String sql = "SELECT * FROM reporte_dano ORDER BY fecha_hora_reporte DESC";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReporteDano r = new ReporteDano();
                r.setId(rs.getInt("id"));
                r.setLaboratorioid(rs.getInt("laboratorio_id"));
                r.setEquipoid(rs.getInt("equipo_id"));
                r.setDocenteid(rs.getInt("docente_id"));
                r.setHorarioid(rs.getInt("horario_id"));
                r.setTipoFalla(rs.getString("tipo_falla"));
                r.setDescripcion(rs.getString("descripcion"));
                java.sql.Timestamp ts = rs.getTimestamp("fecha_hora_reporte");
                r.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                r.setEstadoEncontrado(rs.getString("estado_encontrado"));
                r.setObservaciones(rs.getString("observaciones"));
                lista.add(r);
            }
        }
        return lista;
    }
}
