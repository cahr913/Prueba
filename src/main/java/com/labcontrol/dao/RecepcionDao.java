package com.labcontrol.dao;

import com.labcontrol.modelo.Recepcion;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecepcionDao {

    public void insertar(Recepcion r) throws SQLException {
        // Si no se especificó laboratorio, obtenerlo automáticamente del equipo
        if (r.getLaboratorioId() <= 0 && r.getIdEquipo() > 0) {
            String sqlLab = "SELECT laboratorio_id FROM equipo WHERE id = (?)";
            try (Connection cn = ConexionBD.abrir();
                 PreparedStatement ps = cn.prepareStatement(sqlLab)) {
                ps.setInt(1, r.getIdEquipo());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        r.setLaboratorioId(rs.getInt("laboratorio_id"));
                    }
                }
            }
        }

        String sql = "INSERT INTO recepcion(laboratorio_id, docente_id, horario_id, fecha_hora, estado_general, limpieza, estado_mobiliario, energia_electrica, red, estado_equipos, observaciones) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getLaboratorioId());
            ps.setInt(2, r.getDocenteId());
            ps.setInt(3, r.getHorarioId());
            ps.setTimestamp(4, r.getFechaHora() != null ? java.sql.Timestamp.valueOf(r.getFechaHora()) : null);
            ps.setString(5, r.getEstadoGeneral());
            ps.setString(6, r.getLimpieza());
            ps.setString(7, r.getEstadoMobiliario());
            ps.setBoolean(8, r.isEnergiaElectrica());
            ps.setBoolean(9, r.isRed());
            ps.setString(10, r.getEstadoEquipos());
            ps.setString(11, r.getObservaciones());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Recepcion r) throws SQLException {
        String sql = "UPDATE recepcion SET laboratorio_id=?, docente_id=?, horario_id=?, fecha_hora=?, estado_general=?, limpieza=?, estado_mobiliario=?, energia_electrica=?, red=?, estado_equipos=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, r.getLaboratorioId());
            ps.setInt(2, r.getDocenteId());
            ps.setInt(3, r.getHorarioId());
            ps.setTimestamp(4, r.getFechaHora() != null ? java.sql.Timestamp.valueOf(r.getFechaHora()) : null);
            ps.setString(5, r.getEstadoGeneral());
            ps.setString(6, r.getLimpieza());
            ps.setString(7, r.getEstadoMobiliario());
            ps.setBoolean(8, r.isEnergiaElectrica());
            ps.setBoolean(9, r.isRed());
            ps.setString(10, r.getEstadoEquipos());
            ps.setString(11, r.getObservaciones());
            ps.setInt(12, r.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM recepcion WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Recepcion buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM recepcion WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recepcion r = new Recepcion();
                    r.setId(rs.getInt("id"));
                    r.setLaboratorioId(rs.getInt("laboratorio_id"));
                    r.setDocenteId(rs.getInt("docente_id"));
                    r.setHorarioId(rs.getInt("horario_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
                    r.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                    r.setEstadoGeneral(rs.getString("estado_general"));
                    r.setLimpieza(rs.getString("limpieza"));
                    r.setEstadoMobiliario(rs.getString("estado_mobiliario"));
                    r.setEnergiaElectrica(rs.getBoolean("energia_electrica"));
                    r.setRed(rs.getBoolean("red"));
                    r.setEstadoEquipos(rs.getString("estado_equipos"));
                    r.setObservaciones(rs.getString("observaciones"));
                    return r;
                }
            }
        }
        return null;
    }

    public List<Recepcion> listar() throws SQLException {
        List<Recepcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM recepcion ORDER BY fecha_hora DESC";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Recepcion r = new Recepcion();
                r.setId(rs.getInt("id"));
                r.setLaboratorioId(rs.getInt("laboratorio_id"));
                r.setDocenteId(rs.getInt("docente_id"));
                r.setHorarioId(rs.getInt("horario_id"));
                java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
                r.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                r.setEstadoGeneral(rs.getString("estado_general"));
                r.setLimpieza(rs.getString("limpieza"));
                r.setEstadoMobiliario(rs.getString("estado_mobiliario"));
                r.setEnergiaElectrica(rs.getBoolean("energia_electrica"));
                r.setRed(rs.getBoolean("red"));
                r.setEstadoEquipos(rs.getString("estado_equipos"));
                r.setObservaciones(rs.getString("observaciones"));
                lista.add(r);
            }
        }
        return lista;
    }
}
