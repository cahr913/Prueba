package com.labcontrol.servicio;

import com.labcontrol.dao.MantenimientoDao;
import com.labcontrol.dao.PiezaDao;
import com.labcontrol.modelo.Mantenimiento;
import com.labcontrol.modelo.Pieza;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class MantenimientoService {

    private final MantenimientoDao dao = new MantenimientoDao();

    public void registrar(Mantenimiento mantenimiento) throws SQLException {
        if (mantenimiento == null) {
            throw new IllegalArgumentException("El mantenimiento es obligatorio.");
        }

        for (Mantenimiento existente : listar()) {
            if (existente.getNumeroServicio().equalsIgnoreCase(mantenimiento.getNumeroServicio())) {
                throw new IllegalArgumentException("El número de servicio ya está registrado.");
            }
        }

        dao.insertar(mantenimiento);

        // Persistir piezas cuando se trate de un mantenimiento correctivo.
        if (mantenimiento.getTipo() == Mantenimiento.Tipo.CORRECTIVO) {
            PiezaDao piezaDao = new PiezaDao();
            for (Pieza p : mantenimiento.getPiezas()) {
                p.setMantenimientoId(mantenimiento.getId());
                piezaDao.insertar(p);
            }
        }
    }

    public Optional<Mantenimiento> buscarPorId(int id) throws SQLException {
        Optional<Mantenimiento> opt = Optional.ofNullable(dao.buscarPorId(id));
        opt.ifPresent(m -> {
            try {
                cargarPiezas(m);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return opt;
    }

    public Collection<Mantenimiento> listar() throws SQLException {
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

    private void cargarPiezas(Mantenimiento m) throws SQLException {
        PiezaDao piezaDao = new PiezaDao();
        for (Pieza p : piezaDao.listarPorMantenimiento(m.getId())) {
            m.agregarPieza(p);
        }
    }
}
