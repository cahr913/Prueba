package com.labcontrol.dao;

import com.labcontrol.modelo.Instalacion;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstalacionDao {

    public void insertar(Instalacion i) throws SQLException {
        String sql = "INSERT INTO instalacion(equipo_id, software_id, fecha_instalacion) VALUES (?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getEquipoId());
            ps.setInt(2, i.getSoftwareId());
            ps.setString(3, i.getFechaInstalacion());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Instalacion i) throws SQLException {
        String sql = "UPDATE instalacion SET equipo_id=?, software_id=?, fecha_instalacion=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, i.getEquipoId());
            ps.setInt(2, i.getSoftwareId());
            ps.setString(3, i.getFechaInstalacion());
            ps.setInt(4, i.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM instalacion WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public boolean existe(int equipoId, int softwareId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM instalacion WHERE equipo_id = ? AND software_id = ?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, equipoId);
            ps.setInt(2, softwareId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Instalacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM instalacion WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Instalacion i = new Instalacion();
                    i.setId(rs.getInt("id"));
                    i.setEquipoId(rs.getInt("equipo_id"));
                    i.setSoftwareId(rs.getInt("software_id"));
                    i.setFechaInstalacion(rs.getString("fecha_instalacion"));
                    return i;
                }
            }
        }
        return null;
    }

    public List<Instalacion> listar() throws SQLException {
        List<Instalacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM instalacion ORDER BY id";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Instalacion i = new Instalacion();
                i.setId(rs.getInt("id"));
                i.setEquipoId(rs.getInt("equipo_id"));
                i.setSoftwareId(rs.getInt("software_id"));
                i.setFechaInstalacion(rs.getString("fecha_instalacion"));
                lista.add(i);
            }
        }
        return lista;
    }
}
