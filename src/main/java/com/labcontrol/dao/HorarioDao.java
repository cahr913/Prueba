package com.labcontrol.dao;

import com.labcontrol.modelo.Horario;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HorarioDao {

    public void insertar(Horario h) throws SQLException {
        String sql = "INSERT INTO horario(docente_id, laboratorio_id, curso, fecha, hora_inicio, hora_fin, observaciones) VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getDocenteId());
            ps.setInt(2, h.getLaboratorioId());
            ps.setString(3, h.getCurso());
            ps.setDate(4, h.getFecha() != null ? java.sql.Date.valueOf(h.getFecha()) : null);
            ps.setTime(5, h.getHoraInicio() != null ? java.sql.Time.valueOf(h.getHoraInicio()) : null);
            ps.setTime(6, h.getHoraFin() != null ? java.sql.Time.valueOf(h.getHoraFin()) : null);
            ps.setString(7, h.getObservaciones());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Horario h) throws SQLException {
        String sql = "UPDATE horario SET docente_id=?, laboratorio_id=?, curso=?, fecha=?, hora_inicio=?, hora_fin=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, h.getDocenteId());
            ps.setInt(2, h.getLaboratorioId());
            ps.setString(3, h.getCurso());
            ps.setDate(4, h.getFecha() != null ? java.sql.Date.valueOf(h.getFecha()) : null);
            ps.setTime(5, h.getHoraInicio() != null ? java.sql.Time.valueOf(h.getHoraInicio()) : null);
            ps.setTime(6, h.getHoraFin() != null ? java.sql.Time.valueOf(h.getHoraFin()) : null);
            ps.setString(7, h.getObservaciones());
            ps.setInt(8, h.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM horario WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Horario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM horario WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Horario h = new Horario();
                    h.setId(rs.getInt("id"));
                    h.setDocenteId(rs.getInt("docente_id"));
                    h.setLaboratorioId(rs.getInt("laboratorio_id"));
                    h.setCurso(rs.getString("curso"));
                    java.sql.Date f = rs.getDate("fecha");
                    if (f != null) {
                        LocalDate fecha = f.toLocalDate();
                        h.setFecha(fecha);
                        h.setDia(fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
                    } else {
                        h.setFecha(null);
                        h.setDia(null);
                    }
                    java.sql.Time ti = rs.getTime("hora_inicio");
                    h.setHoraInicio(ti != null ? ti.toLocalTime() : null);
                    java.sql.Time tf = rs.getTime("hora_fin");
                    h.setHoraFin(tf != null ? tf.toLocalTime() : null);
                    h.setObservaciones(rs.getString("observaciones"));
                    return h;
                }
            }
        }
        return null;
    }

    public List<Horario> listar() throws SQLException {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT * FROM horario ORDER BY fecha, hora_inicio";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDocenteId(rs.getInt("docente_id"));
                h.setLaboratorioId(rs.getInt("laboratorio_id"));
                h.setCurso(rs.getString("curso"));
                java.sql.Date f = rs.getDate("fecha");
                if (f != null) {
                    LocalDate fecha = f.toLocalDate();
                    h.setFecha(fecha);
                    h.setDia(fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
                } else {
                    h.setFecha(null);
                    h.setDia(null);
                }
                java.sql.Time ti = rs.getTime("hora_inicio");
                h.setHoraInicio(ti != null ? ti.toLocalTime() : null);
                java.sql.Time tf = rs.getTime("hora_fin");
                h.setHoraFin(tf != null ? tf.toLocalTime() : null);
                h.setObservaciones(rs.getString("observaciones"));
                lista.add(h);
            }
        }
        return lista;
    }

    public boolean existeSolapamiento(int laboratorioId, String diaSemana, LocalTime inicio, LocalTime fin, int horarioExceptuado) throws SQLException {
        // Obtener todos los horarios del laboratorio y comparar por día de la semana en Java
        String sql = "SELECT id, hora_inicio, hora_fin, fecha FROM horario WHERE laboratorio_id = ? AND id <> ?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, laboratorioId);
            ps.setInt(2, horarioExceptuado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idExistente = rs.getInt("id");
                    java.sql.Time hi = rs.getTime("hora_inicio");
                    java.sql.Time hf = rs.getTime("hora_fin");
                    java.sql.Date f = rs.getDate("fecha");
                    // Calcular el día de la semana desde la fecha
                    String diaExistente = null;
                    if (f != null) {
                        diaExistente = f.toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                    }
                    if (diaExistente != null && diaExistente.equalsIgnoreCase(diaSemana)) {
                        LocalTime hiLocal = hi != null ? hi.toLocalTime() : null;
                        LocalTime hfLocal = hf != null ? hf.toLocalTime() : null;
                        if (hiLocal != null && hfLocal != null) {
                            // Superposición: inicio < hfExistente && hiExistente < fin
                            if (inicio.isBefore(hfLocal) && hiLocal.isBefore(fin)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
