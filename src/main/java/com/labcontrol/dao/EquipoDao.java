package com.labcontrol.dao;

import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Computadora;
import com.labcontrol.db.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDao {

    public void insertar(Equipo e) throws SQLException {
        String sql = "INSERT INTO equipo(codigo_inventario, tipo, num_serie, marca, modelo, fecha_adquisicion, estado, observaciones, laboratorio_id) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getCodigoInventario());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getNumSerie());
            ps.setString(4, e.getMarca());
            ps.setString(5, e.getModelo());
            ps.setString(6, e.getFechaAdquisicion());
            ps.setString(7, e.getEstado());
            ps.setString(8, e.getObservaciones());
            if (e.getLaboratorioId() > 0) {
                ps.setInt(9, e.getLaboratorioId());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ningún registro");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Equipo e) throws SQLException {
        String sql = "UPDATE equipo SET codigo_inventario=?, tipo=?, num_serie=?, marca=?, modelo=?, fecha_adquisicion=?, estado=?, observaciones=?, laboratorio_id=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getCodigoInventario());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getNumSerie());
            ps.setString(4, e.getMarca());
            ps.setString(5, e.getModelo());
            ps.setString(6, e.getFechaAdquisicion());
            ps.setString(7, e.getEstado());
            ps.setString(8, e.getObservaciones());
            if (e.getLaboratorioId() > 0) {
                ps.setInt(9, e.getLaboratorioId());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            ps.setInt(10, e.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ningún registro");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM equipo WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se eliminó ningún registro");
            }
        }
    }

    public Equipo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM equipo WHERE id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Equipo e = new Equipo();
                    e.setId(rs.getInt("id"));
                    e.setCodigoInventario(rs.getString("codigo_inventario"));
                    e.setTipo(rs.getString("tipo"));
                    e.setNumSerie(rs.getString("num_serie"));
                    e.setMarca(rs.getString("marca"));
                    e.setModelo(rs.getString("modelo"));
                    e.setFechaAdquisicion(rs.getString("fecha_adquisicion"));
                    e.setEstado(rs.getString("estado"));
                    e.setObservaciones(rs.getString("observaciones"));
                    e.setLaboratorioId(rs.getInt("laboratorio_id"));
                    return e;
                }
            }
        }
        return null;
    }

    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipo ORDER BY codigo_inventario";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo e = new Equipo();
                e.setId(rs.getInt("id"));
                e.setCodigoInventario(rs.getString("codigo_inventario"));
                e.setTipo(rs.getString("tipo"));
                e.setNumSerie(rs.getString("num_serie"));
                e.setMarca(rs.getString("marca"));
                e.setModelo(rs.getString("modelo"));
                e.setFechaAdquisicion(rs.getString("fecha_adquisicion"));
                e.setEstado(rs.getString("estado"));
                e.setObservaciones(rs.getString("observaciones"));
                e.setLaboratorioId(rs.getInt("laboratorio_id"));
                lista.add(e);
            }
        }
        return lista;
    }

    public boolean existeCodigo(String codigo, int idActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM equipo WHERE codigo_inventario = ? AND id <> ?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, idActual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Computadora> listarComputadoras() throws SQLException {
        List<Computadora> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.codigo_inventario, e.tipo, e.num_serie, e.marca, e.modelo, "
                + "e.fecha_adquisicion, e.estado, e.observaciones, e.laboratorio_id, "
                + "c.hostname, c.ip, c.sistema_operativo, c.arquitectura, c.caracteristicas "
                + "FROM equipo e JOIN computadora c ON c.id = e.id ORDER BY e.codigo_inventario";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Computadora c = new Computadora();
                c.setId(rs.getInt("id"));
                c.setCodigoInventario(rs.getString("codigo_inventario"));
                c.setTipo(rs.getString("tipo"));
                c.setNumSerie(rs.getString("num_serie"));
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                c.setFechaAdquisicion(rs.getString("fecha_adquisicion"));
                c.setEstado(rs.getString("estado"));
                c.setObservaciones(rs.getString("observaciones"));
                c.setLaboratorioId(rs.getInt("laboratorio_id"));
                c.setHostname(rs.getString("hostname"));
                c.setIp(rs.getString("ip"));
                c.setSistemaOperativo(rs.getString("sistema_operativo"));
                c.setArquitectura(rs.getString("arquitectura"));
                c.setCaracteristicas(rs.getString("caracteristicas"));
                lista.add(c);
            }
        }
        return lista;
    }

    public void actualizarEstado(int equipoId, String estado) throws SQLException {
    String sql = "UPDATE equipo SET estado = ? WHERE id = ?";
    try (Connection cn = ConexionBD.abrir();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, estado);
        ps.setInt(2, equipoId);
        ps.executeUpdate();
    }
}
    
}
