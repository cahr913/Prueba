package com.labcontrol.dao;

import com.labcontrol.modelo.Usuario;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario(nombre, apellido, nombre_usuario, contrasena, correo, rol, estado, fecha_creacion) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getRol());
            ps.setBoolean(7, u.isEstado());
            LocalDate fc = u.getFechaCreacion();
            ps.setDate(8, fc != null ? java.sql.Date.valueOf(fc) : null);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
            }
        }
    }

    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nombre=?, apellido=?, nombre_usuario=?, contrasena=?, correo=?, rol=?, estado=?, fecha_creacion=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getRol());
            ps.setBoolean(7, u.isEstado());
            LocalDate fc = u.getFechaCreacion();
            ps.setDate(8, fc != null ? java.sql.Date.valueOf(fc) : null);
            ps.setInt(9, u.getIdUsuario());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario() {};
                    u.setIdUsuario(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getBoolean("estado"));
                    java.sql.Date fd = rs.getDate("fecha_creacion");
                    u.setFechaCreacion(fd != null ? fd.toLocalDate() : null);
                    return u;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario() {};
                    u.setIdUsuario(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getBoolean("estado"));
                    java.sql.Date fd = rs.getDate("fecha_creacion");
                    u.setFechaCreacion(fd != null ? fd.toLocalDate() : null);
                    return u;
                }
            }
        }
        return null;
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario() {};
                u.setIdUsuario(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setUsuario(rs.getString("nombre_usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getBoolean("estado"));
                java.sql.Date fd = rs.getDate("fecha_creacion");
                u.setFechaCreacion(fd != null ? fd.toLocalDate() : null);
                lista.add(u);
            }
        }
        return lista;
    }

    public List<Usuario> listarPorRol(String rol) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE rol=? ORDER BY nombre";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario() {};
                    u.setIdUsuario(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getBoolean("estado"));
                    java.sql.Date fd = rs.getDate("fecha_creacion");
                    u.setFechaCreacion(fd != null ? fd.toLocalDate() : null);
                    lista.add(u);
                }
            }
        }
        return lista;
    }
}
