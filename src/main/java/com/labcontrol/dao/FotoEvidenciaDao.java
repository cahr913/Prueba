package com.labcontrol.dao;

import com.labcontrol.modelo.FotoEvidencia;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FotoEvidenciaDao {

    public void insertar(FotoEvidencia f) throws SQLException {
        String sql = "INSERT INTO foto_evidencia(reporte_dano_id, equipo_id, ruta_archivo, descripcion) VALUES (?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getReporteid());
            ps.setInt(2, f.getEquipoid());
            ps.setString(3, f.getRutaArchivo());
            ps.setString(4, f.getDescripcion());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(FotoEvidencia f) throws SQLException {
        String sql = "UPDATE foto_evidencia SET reporte_dano_id=?, equipo_id=?, ruta_archivo=?, descripcion=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, f.getReporteid());
            ps.setInt(2, f.getEquipoid());
            ps.setString(3, f.getRutaArchivo());
            ps.setString(4, f.getDescripcion());
            ps.setInt(5, f.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM foto_evidencia WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public FotoEvidencia buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM foto_evidencia WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FotoEvidencia f = new FotoEvidencia();
                    f.setId(rs.getInt("id"));
                    f.setReporteid(rs.getInt("reporte_dano_id"));
                    f.setEquipoid(rs.getInt("equipo_id"));
                    f.setRutaArchivo(rs.getString("ruta_archivo"));
                    f.setDescripcion(rs.getString("descripcion"));
                    return f;
                }
            }
        }
        return null;
    }

    public List<FotoEvidencia> listar() throws SQLException {
        List<FotoEvidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM foto_evidencia ORDER BY id";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FotoEvidencia f = new FotoEvidencia();
                f.setId(rs.getInt("id"));
                f.setReporteid(rs.getInt("reporte_dano_id"));
                f.setEquipoid(rs.getInt("equipo_id"));
                f.setRutaArchivo(rs.getString("ruta_archivo"));
                f.setDescripcion(rs.getString("descripcion"));
                lista.add(f);
            }
        }
        return lista;
    }
}
