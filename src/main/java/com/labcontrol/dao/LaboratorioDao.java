package com.labcontrol.dao;

import com.labcontrol.modelo.*;

import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaboratorioDao {

    public void insertar(Laboratorio l) throws SQLException {
        String codigo = l.getCodigo();
        if (codigo == null || codigo.isBlank()) {
            codigo = generarSiguienteCodigo();
            l.setCodigo(codigo);
        }
        String sql = "INSERT INTO laboratorio(codigo, nombre, ubicacion, capacidad, estado, responsable, observaciones, centro_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getCodigo());
            ps.setString(2, l.getNombre());
            ps.setString(3, l.getUbicacion());
            ps.setInt(4, l.getCapacidad());
            ps.setString(5, l.getEstado());
            ps.setString(6, l.getResponsable());
            ps.setString(7, l.getObservaciones());
            ps.setInt(8, l.getCentroId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) l.setId(rs.getInt(1));
            }
        }
    }

    private String generarSiguienteCodigo() throws SQLException {
        String sql = "SELECT codigo FROM laboratorio ORDER BY id DESC LIMIT 1";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String ultimo = rs.getString("codigo");
                if (ultimo != null && ultimo.startsWith("LAB-")) {
                    try {
                        int num = Integer.parseInt(ultimo.substring(4)) + 1;
                        return String.format("LAB-%03d", num);
                    } catch (NumberFormatException e) {
                        return "LAB-001";
                    }
                }
            }
        }
        return "LAB-001";
    }

    public void actualizar(Laboratorio l) throws SQLException {
        String sql = "UPDATE laboratorio SET codigo=?, nombre=?, ubicacion=?, capacidad=?, estado=?, responsable=?, observaciones=?, centro_id=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, l.getCodigo());
            ps.setString(2, l.getNombre());
            ps.setString(3, l.getUbicacion());
            ps.setInt(4, l.getCapacidad());
            ps.setString(5, l.getEstado());
            ps.setString(6, l.getResponsable());
            ps.setString(7, l.getObservaciones());
            ps.setInt(8, l.getCentroId());
            ps.setInt(9, l.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM laboratorio WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Laboratorio buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM laboratorio WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Laboratorio l = new Laboratorio();
                    l.setId(rs.getInt("id"));
                    l.setCodigo(rs.getString("codigo"));
                    l.setNombre(rs.getString("nombre"));
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setCapacidad(rs.getInt("capacidad"));
                    l.setEstado(rs.getString("estado"));
                    l.setResponsable(rs.getString("responsable"));
                    l.setObservaciones(rs.getString("observaciones"));
                    l.setCentroId(rs.getInt("centro_id"));
                    return l;
                }
            }
        }
        return null;
    }

    public Laboratorio obtenerOCrear(String codigo) throws SQLException {
        if (codigo == null || codigo.isBlank()) {
            throw new SQLException("El código de laboratorio es obligatorio.");
        }
        Laboratorio existente = buscarPorCodigo(codigo);
        if (existente != null) {
            return existente;
        }
        Laboratorio l = new Laboratorio();
        l.setCodigo(codigo);
        l.setNombre("Laboratorio " + codigo);
        l.setUbicacion("");
        l.setCapacidad(0);
        l.setEstado("Activo");
        l.setResponsable("");
        l.setObservaciones("");
        l.setCentroId(1);
        insertar(l);
        return l;
    }

    public Laboratorio buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM laboratorio WHERE codigo = ?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Laboratorio l = new Laboratorio();
                    l.setId(rs.getInt("id"));
                    l.setCodigo(rs.getString("codigo"));
                    l.setNombre(rs.getString("nombre"));
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setCapacidad(rs.getInt("capacidad"));
                    l.setEstado(rs.getString("estado"));
                    l.setResponsable(rs.getString("responsable"));
                    l.setObservaciones(rs.getString("observaciones"));
                    l.setCentroId(rs.getInt("centro_id"));
                    return l;
                }
            }
        }
        return null;
    }

    public List<Laboratorio> listar() throws SQLException {
        List<Laboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM laboratorio ORDER BY nombre";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Laboratorio l = new Laboratorio();
                l.setId(rs.getInt("id"));
                l.setCodigo(rs.getString("codigo"));
                l.setNombre(rs.getString("nombre"));
                l.setUbicacion(rs.getString("ubicacion"));
                l.setCapacidad(rs.getInt("capacidad"));
                l.setEstado(rs.getString("estado"));
                l.setResponsable(rs.getString("responsable"));
                l.setObservaciones(rs.getString("observaciones"));
                l.setCentroId(rs.getInt("centro_id"));
                lista.add(l);
            }
        }
        return lista;
    }
}
