package com.labcontrol.dao;

import com.labcontrol.modelo.Software;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SoftwareDao {

    public void insertar(Software s) throws SQLException {
        String sql = "INSERT INTO software(nombre, version, tipo, tipo_licencia, estado) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getVersion());
            ps.setString(3, s.getTipo());
            ps.setString(4, s.getTipoLicencia());
            ps.setString(5, s.getEstado());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Software s) throws SQLException {
        String sql = "UPDATE software SET nombre=?, version=?, tipo=?, tipo_licencia=?, estado=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getVersion());
            ps.setString(3, s.getTipo());
            ps.setString(4, s.getTipoLicencia());
            ps.setString(5, s.getEstado());
            ps.setInt(6, s.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM software WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public boolean existeNombreVersion(String nombre, String version, int idActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM software WHERE nombre = ? AND version = ? AND id <> ?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, version);
            ps.setInt(3, idActual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Software buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM software WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Software s = new Software();
                    s.setId(rs.getInt("id"));
                    s.setNombre(rs.getString("nombre"));
                    s.setVersion(rs.getString("version"));
                    s.setTipo(rs.getString("tipo"));
                    s.setTipoLicencia(rs.getString("tipo_licencia"));
                    s.setEstado(rs.getString("estado"));
                    return s;
                }
            }
        }
        return null;
    }

    public List<Software> listar() throws SQLException {
        List<Software> lista = new ArrayList<>();
        String sql = "SELECT * FROM software ORDER BY nombre";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Software s = new Software();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setVersion(rs.getString("version"));
                s.setTipo(rs.getString("tipo"));
                s.setTipoLicencia(rs.getString("tipo_licencia"));
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        }
        return lista;
    }
}
