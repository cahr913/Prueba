package com.labcontrol.dao;

import com.labcontrol.db.ConexionBD;
import com.labcontrol.modelo.CentroEducativo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentroEducativoDao {

    public void insertar(CentroEducativo c) throws SQLException {
        String sql = "INSERT INTO centro_educativo (codigo_institucional,nombre,direccion,telefono,correo,director,logo_path) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = ConexionBD.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCodigoInstitucional());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getDirector());
            ps.setString(7, c.getRutaLogo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    public CentroEducativo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM centro_educativo WHERE id = ?";
        try (Connection con = ConexionBD.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CentroEducativo c = new CentroEducativo();
                    c.setId(rs.getInt("id"));
                    c.setCodigoInstitucional(rs.getString("codigo_institucional"));
                    c.setNombre(rs.getString("nombre"));
                    c.setDireccion(rs.getString("direccion"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreo(rs.getString("correo"));
                    c.setDirector(rs.getString("director"));
                    c.setRutaLogo(rs.getString("logo_path"));
                    return c;
                }
            }
        }
        return null;
    }

    public List<CentroEducativo> listar() throws SQLException {
        List<CentroEducativo> lista = new ArrayList<>();
        String sql = "SELECT * FROM centro_educativo ORDER BY nombre";
        try (Connection con = ConexionBD.abrir(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CentroEducativo c = new CentroEducativo();
                c.setId(rs.getInt("id"));
                c.setCodigoInstitucional(rs.getString("codigo_institucional"));
                c.setNombre(rs.getString("nombre"));
                c.setDireccion(rs.getString("direccion"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                c.setDirector(rs.getString("director"));
                c.setRutaLogo(rs.getString("logo_path"));
                lista.add(c);
            }
        }
        return lista;
    }

    public void actualizar(CentroEducativo c) throws SQLException {
        String sql = "UPDATE centro_educativo SET codigo_institucional=?, nombre=?, direccion=?, telefono=?, correo=?, director=?, logo_path=? WHERE id=?";
        try (Connection con = ConexionBD.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCodigoInstitucional());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getDirector());
            ps.setString(7, c.getRutaLogo());
            ps.setInt(8, c.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM centro_educativo WHERE id=?";
        try (Connection con = ConexionBD.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}