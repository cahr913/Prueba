package com.labcontrol.dao;

import com.labcontrol.modelo.BitacoraAcceso;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class BitacoraAccesoDao {

    public void insertar(BitacoraAcceso b) throws SQLException {
        String sql = "INSERT INTO bitacora_acceso(fecha_hora, accion, ip, dispositivo) VALUES (?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, b.getFechaHora() != null ? Timestamp.valueOf(b.getFechaHora()) : null);
            ps.setString(2, b.getAccion());
            ps.setString(3, b.getIp());
            ps.setString(4, b.getDispositivo());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setIdBitacora(rs.getInt(1));
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM bitacora_acceso WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public BitacoraAcceso buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM bitacora_acceso WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BitacoraAcceso b = new BitacoraAcceso();
                    b.setIdBitacora(rs.getInt("id"));
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
                    b.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                    b.setAccion(rs.getString("accion"));
                    b.setIp(rs.getString("ip"));
                    b.setDispositivo(rs.getString("dispositivo"));
                    return b;
                }
            }
        }
        return null;
    }

    public List<BitacoraAcceso> listar() throws SQLException {
        List<BitacoraAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM bitacora_acceso ORDER BY fecha_hora DESC";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BitacoraAcceso b = new BitacoraAcceso();
                b.setIdBitacora(rs.getInt("id"));
                java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
                b.setFechaHora(ts != null ? ts.toLocalDateTime() : null);
                b.setAccion(rs.getString("accion"));
                b.setIp(rs.getString("ip"));
                b.setDispositivo(rs.getString("dispositivo"));
                lista.add(b);
            }
        }
        return lista;
    }
}
