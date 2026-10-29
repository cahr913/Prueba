package com.labcontrol.servicio;

import com.labcontrol.dao.PiezaDao;
import com.labcontrol.modelo.Pieza;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class PiezaService {

    private final PiezaDao dao = new PiezaDao();

    public void registrar(Pieza pieza) throws SQLException {
        if (pieza == null) {
            throw new IllegalArgumentException("La pieza es obligatoria.");
        }
        dao.insertar(pieza);
    }

    public Optional<Pieza> buscarPorId(int id) throws SQLException {
        return Optional.ofNullable(dao.buscarPorId(id));
    }

    public Collection<Pieza> listar() throws SQLException {
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
