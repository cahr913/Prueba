package com.labcontrol.dao;

import com.labcontrol.modelo.*;
import com.labcontrol.db.*;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;


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

}
