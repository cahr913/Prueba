package com.labcontrol.servicio;

import com.labcontrol.dao.RecepcionDao;
import com.labcontrol.modelo.Recepcion;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class RecepcionService {

    private final RecepcionDao dao = new RecepcionDao();

    public void registrar(Recepcion recepcion) throws SQLException {
        if (recepcion == null) {
            throw new IllegalArgumentException("La recepción es obligatoria.");
        }
        dao.insertar(recepcion);
    }

    public Optional<Recepcion> buscarPorId(int id) throws SQLException {
        return Optional.ofNullable(dao.buscarPorId(id));
    }

    public Collection<Recepcion> listar() throws SQLException {
        return dao.listar();
    }

    public boolean eliminar(int id) throws SQLException {
        try {
            dao.eliminar(id);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
