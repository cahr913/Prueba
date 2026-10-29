package com.labcontrol.dao;

import com.labcontrol.modelo.Pieza;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiezaDao {

    public void insertar(Pieza p) throws SQLException {
        String sql = "INSERT INTO pieza(mantenimiento_id, nombre, descripcion, cantidad, accion, observaciones) VALUES (?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getMantenimientoId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getCantidad());
            ps.setString(5, p.getAccion() != null ? p.getAccion().name() : null);
            ps.setString(6, p.getObservaciones());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Pieza p) throws SQLException {
        String sql = "UPDATE pieza SET mantenimiento_id=?, nombre=?, descripcion=?, cantidad=?, accion=?, observaciones=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getMantenimientoId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getCantidad());
            ps.setString(5, p.getAccion() != null ? p.getAccion().name() : null);
            ps.setString(6, p.getObservaciones());
            ps.setInt(7, p.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pieza WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public List<Pieza> listarPorMantenimiento(int mantenimientoId) throws SQLException {
        List<Pieza> lista = new ArrayList<>();
        String sql = "SELECT * FROM pieza WHERE mantenimiento_id = (?) ORDER BY id";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mantenimientoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pieza p = new Pieza();
                    p.setId(rs.getInt("id"));
                    p.setMantenimientoId(rs.getInt("mantenimiento_id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setCantidad(rs.getInt("cantidad"));
                    String accionStr = rs.getString("accion");
                    p.setAccion(accionStr != null ? com.labcontrol.modelo.Accion.valueOf(accionStr) : null);
                    p.setObservaciones(rs.getString("observaciones"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public Pieza buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pieza WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pieza p = new Pieza();
                    p.setId(rs.getInt("id"));
                    p.setMantenimientoId(rs.getInt("mantenimiento_id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setCantidad(rs.getInt("cantidad"));
                    String accionStr = rs.getString("accion");
                    p.setAccion(accionStr != null ? com.labcontrol.modelo.Accion.valueOf(accionStr) : null);
                    p.setObservaciones(rs.getString("observaciones"));
                    return p;
                }
            }
        }
        return null;
    }

    public List<Pieza> listar() throws SQLException {
        List<Pieza> lista = new ArrayList<>();
        String sql = "SELECT * FROM pieza ORDER BY id";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pieza p = new Pieza();
                p.setId(rs.getInt("id"));
                p.setMantenimientoId(rs.getInt("mantenimiento_id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setCantidad(rs.getInt("cantidad"));
                String accionStr = rs.getString("accion");
                p.setAccion(accionStr != null ? com.labcontrol.modelo.Accion.valueOf(accionStr) : null);
                p.setObservaciones(rs.getString("observaciones"));
                lista.add(p);
            }
        }
        return lista;
    }
}
