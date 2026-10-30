package com.labcontrol.dao;

import com.labcontrol.modelo.Recepcion;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecepcionDao {

    public void insertar(Recepcion r) throws SQLException {
        String sql = "INSERT INTO recepcion(laboratorio_id, docente_id, horario_id, fecha_hora, estado_general, "
                + "limpieza, estado_mobiliario, energia_electrica, red, estado_equipos, observaciones) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenar(ps, r, 1);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
        guardarEquiposDanados(r);
    }

    public void actualizar(Recepcion r) throws SQLException {
        String sql = "UPDATE recepcion SET laboratorio_id=?, docente_id=?, horario_id=?, fecha_hora=?, "
                + "estado_general=?, limpieza=?, estado_mobiliario=?, energia_electrica=?, red=?, "
                + "estado_equipos=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            int siguiente = llenar(ps, r, 1);
            ps.setInt(siguiente, r.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
        guardarEquiposDanados(r);
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
                    return leer(rs);
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
                lista.add(leer(rs));
            }
        }
        return lista;
    }

    
    public List<Integer> listarEquiposDanados(int recepcionId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT equipo_id FROM recepcion_equipo_dano WHERE recepcion_id = (?) ORDER BY id";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, recepcionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("equipo_id"));
                }
            }
        }
        return ids;
    }


    private int llenar(PreparedStatement ps, Recepcion r, int desde) throws SQLException {
        ps.setInt(desde, r.getLaboratorioId());
        ps.setInt(desde + 1, r.getDocenteId());
        if (r.getHorarioId() > 0) {
            ps.setInt(desde + 2, r.getHorarioId());
        } else {
            ps.setNull(desde + 2, java.sql.Types.INTEGER);
        }
        ps.setTimestamp(desde + 3, r.getFechaHora() != null ? java.sql.Timestamp.valueOf(r.getFechaHora()) : null);
        ps.setString(desde + 4, r.getEstadoGeneral());
        ps.setString(desde + 5, r.getLimpieza());
        ps.setString(desde + 6, r.getEstadoMobiliario());
        ps.setBoolean(desde + 7, r.isEnergiaElectrica());
        ps.setBoolean(desde + 8, r.isRed());
        ps.setString(desde + 9, r.getEstadoEquipos());
        ps.setString(desde + 10, r.getObservaciones());
        return desde + 11;
    }

    
    private void guardarEquiposDanados(Recepcion r) throws SQLException {
        if (r.getId() <= 0) {
            return;
        }
        try (Connection cn = ConexionBD.abrir()) {
            try (PreparedStatement ps = cn.prepareStatement("DELETE FROM recepcion_equipo_dano WHERE recepcion_id = ?")) {
                ps.setInt(1, r.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO recepcion_equipo_dano(recepcion_id, equipo_id) VALUES (?,?)")) {
                for (int equipoId : r.getEquiposDanadosIds()) {
                    ps.setInt(1, r.getId());
                    ps.setInt(2, equipoId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private Recepcion leer(ResultSet rs) throws SQLException {
        Recepcion r = new Recepcion();
        r.setId(rs.getInt("id"));
        r.setLaboratorioId(rs.getInt("laboratorio_id"));
        r.setDocenteId(rs.getInt("docente_id"));
        int horario = rs.getInt("horario_id");
        r.setHorarioId(rs.wasNull() ? 0 : horario);
        java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
        r.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
        r.setEstadoGeneral(rs.getString("estado_general"));
        r.setLimpieza(rs.getString("limpieza"));
        r.setEstadoMobiliario(rs.getString("estado_mobiliario"));
        r.setEnergiaElectrica(rs.getBoolean("energia_electrica"));
        r.setRed(rs.getBoolean("red"));
        r.setEstadoEquipos(rs.getString("estado_equipos"));
        r.setObservaciones(rs.getString("observaciones"));
        r.setEquiposDanadosIds(listarEquiposDanados(r.getId()));
        return r;
    }
}
