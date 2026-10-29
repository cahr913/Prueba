package com.labcontrol.dao;

import com.labcontrol.modelo.Computadora;
import com.labcontrol.db.ConexionBD;
import java.sql.*;

public class ComputadoraDao {

    public void insertar(Computadora c) throws SQLException {
        new EquipoDao().insertar(c);
        String sql = "INSERT INTO computadora(id, hostname, ip, sistema_operativo, arquitectura, caracteristicas) VALUES (?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getHostname());
            ps.setString(3, c.getIp());
            ps.setString(4, c.getSistemaOperativo());
            ps.setString(5, c.getArquitectura());
            ps.setString(6, c.getCaracteristicas());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se almacenó ninguna computadora");
            }
        }
    }

    public void actualizar(Computadora c) throws SQLException {
        new EquipoDao().actualizar(c);
        String sql = "UPDATE computadora SET hostname=?, ip=?, sistema_operativo=?, arquitectura=?, caracteristicas=? WHERE id=?";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getHostname());
            ps.setString(2, c.getIp());
            ps.setString(3, c.getSistemaOperativo());
            ps.setString(4, c.getArquitectura());
            ps.setString(5, c.getCaracteristicas());
            ps.setInt(6, c.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ninguna computadora");
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        new EquipoDao().eliminar(id);
    }

    public Computadora buscarPorId(int id) throws SQLException {
        String sql = "SELECT e.*, c.hostname, c.ip, c.sistema_operativo, c.arquitectura, c.caracteristicas FROM equipo e JOIN computadora c ON c.id = e.id WHERE e.id = (?)";
        try (Connection cn = ConexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                    return c;
                }
            }
        }
        return null;
    }

    public java.util.List<Computadora> listar() throws SQLException {
        java.util.List<Computadora> lista = new java.util.ArrayList<>();
        String sql = "SELECT e.*, c.hostname, c.ip, c.sistema_operativo, c.arquitectura, c.caracteristicas FROM equipo e JOIN computadora c ON c.id = e.id ORDER BY e.codigo_inventario";
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
}
