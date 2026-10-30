package com.labcontrol.servicio;

import com.labcontrol.dao.MantenimientoDao;
import com.labcontrol.dao.PiezaDao;
import com.labcontrol.modelo.Pieza;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PiezaServicio {

    private final PiezaDao dao = new PiezaDao();
    private final MantenimientoDao mantenimientoDao = new MantenimientoDao();

    public void registrar(Pieza pieza) throws SQLException {
        validar(pieza);
        dao.insertar(pieza);
    }

    public void actualizar(Pieza pieza) throws SQLException {
        validar(pieza);
        if (pieza.getId() <= 0) {
            throw new IllegalArgumentException("Seleccione la pieza que desea actualizar.");
        }
        dao.actualizar(pieza);
    }

    public Optional<Pieza> buscarPorId(int id) throws SQLException {
        return Optional.ofNullable(dao.buscarPorId(id));
    }

    public Collection<Pieza> listar() throws SQLException {
        return dao.listar();
    }

    public List<Pieza> listarPorMantenimiento(int mantenimientoId) throws SQLException {
        return dao.listarPorMantenimiento(mantenimientoId);
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }

    private void validar(Pieza p) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("La pieza es obligatoria.");
        }
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la pieza es obligatorio.");
        }
        if (p.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (p.getAccion() == null) {
            throw new IllegalArgumentException("Debe indicar la acción: reparación o reemplazo.");
        }
        if (p.getMantenimientoId() <= 0 || mantenimientoDao.buscarPorId(p.getMantenimientoId()) == null) {
            throw new IllegalArgumentException("Seleccione un mantenimiento válido para la pieza.");
        }
    }
}
