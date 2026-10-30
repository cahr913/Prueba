package com.labcontrol.servicio;

import com.labcontrol.modelo.*;
import com.labcontrol.dao.*;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class MantenimientoServicio {

    private final MantenimientoDao dao = new MantenimientoDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final PiezaDao piezaDao = new PiezaDao();

    public void registrar(Mantenimiento m) throws SQLException {
        if (m == null) {
            throw new IllegalArgumentException("El mantenimiento es obligatorio.");
        }
        if (m.getNumeroServicio() == null || m.getNumeroServicio().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de servicio es obligatorio.");
        }
        if (m.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        }

        Equipo equipo = equipoDao.buscarPorId(m.getEquipoId());
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no existe.");
        }
        Usuario responsable = usuarioDao.buscarPorId(m.getResponsableId());
        if (responsable == null) {
            throw new IllegalArgumentException("El responsable no existe.");
        }
        Mantenimiento existente = dao.buscarPorNumeroServicio(m.getNumeroServicio().trim());
        if (existente != null) {
            throw new IllegalArgumentException("El numero de servicio ya esta registrado.");
        }

        if (m instanceof MantenimientoCorrectivo && m.getEstado() == Mantenimiento.Estado.FINALIZADO && m.getPiezas().isEmpty()) {
            throw new IllegalStateException("No se puede finalizar un correctivo sin piezas.");
        }

        dao.insertar(m);

        
        for (Pieza p : m.getPiezas()) {
            if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("Toda pieza debe tener nombre.");
            }
            if (p.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad de la pieza debe ser mayor que cero.");
            }
            if (p.getAccion() == null) {
                throw new IllegalArgumentException("Debe indicar la accion de la pieza.");
            }
            p.setMantenimientoId(m.getId());
            piezaDao.insertar(p);
        }

        if (m.getEstado() == Mantenimiento.Estado.FINALIZADO) {
            String estadoFinal = m.getEstadoFinalEquipo();
            if (estadoFinal == null || estadoFinal.trim().isEmpty()) {
                estadoFinal = Equipo.ACTIVO;
            }
            equipo.cambiarEstado(estadoFinal);
        } else {
            equipo.cambiarEstado(Equipo.MANTENIMIENTO);
        }
        equipoDao.actualizar(equipo);
    }

    public void actualizar(Mantenimiento m) throws SQLException {
        if (m == null || m.getId() <= 0) {
            throw new IllegalArgumentException("Seleccione el mantenimiento que desea actualizar.");
        }
        if (m.getNumeroServicio() == null || m.getNumeroServicio().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de servicio es obligatorio.");
        }
        if (m.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        }

        Equipo equipo = equipoDao.buscarPorId(m.getEquipoId());
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no existe.");
        }
        Usuario responsable = usuarioDao.buscarPorId(m.getResponsableId());
        if (responsable == null) {
            throw new IllegalArgumentException("El responsable no existe.");
        }

        if (m instanceof MantenimientoCorrectivo && m.getEstado() == Mantenimiento.Estado.FINALIZADO
                && m.getPiezas().isEmpty()) {
            throw new IllegalStateException("No se puede finalizar un correctivo sin piezas.");
        }

        dao.actualizar(m);

        try (java.sql.Connection cn = com.labcontrol.db.ConexionBD.abrir();
             java.sql.PreparedStatement ps = cn.prepareStatement("DELETE FROM pieza WHERE mantenimiento_id = ?")) {
            ps.setInt(1, m.getId());
            ps.executeUpdate();
        }
        for (Pieza p : m.getPiezas()) {
            if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("Toda pieza debe tener nombre.");
            }
            if (p.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad de la pieza debe ser mayor que cero.");
            }
            if (p.getAccion() == null) {
                throw new IllegalArgumentException("Debe indicar la accion de la pieza.");
            }
            p.setMantenimientoId(m.getId());
            piezaDao.insertar(p);
        }

        if (m.getEstado() == Mantenimiento.Estado.FINALIZADO) {
            String estadoFinal = m.getEstadoFinalEquipo();
            if (estadoFinal == null || estadoFinal.trim().isEmpty()) {
                estadoFinal = Equipo.ACTIVO;
            }
            equipo.cambiarEstado(estadoFinal);
        } else {
            equipo.cambiarEstado(Equipo.MANTENIMIENTO);
        }
        equipoDao.actualizar(equipo);
    }

    public Optional<Mantenimiento> buscarPorId(int id) throws SQLException {
        Mantenimiento m = dao.buscarPorId(id);
        if (m != null) {
            for (Pieza p : piezaDao.listarPorMantenimiento(m.getId())) {
                m.agregarPieza(p);
            }
        }
        return Optional.ofNullable(m);
    }

    public Collection<Mantenimiento> listar() throws SQLException {
        return dao.listar();
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }
}
