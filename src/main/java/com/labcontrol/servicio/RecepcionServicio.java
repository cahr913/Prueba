package com.labcontrol.servicio;

import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.RecepcionDao;
import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Recepcion;
import com.labcontrol.modelo.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RecepcionServicio {

    private final RecepcionDao dao = new RecepcionDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final EquipoDao equipoDao = new EquipoDao();

    public void registrar(Recepcion recepcion) throws SQLException {
        validar(recepcion);
        dao.insertar(recepcion);
        // Marcar los equipos dañados como DAÑADO
        for (int equipoId : recepcion.getEquiposDanadosIds()) {
            Equipo equipo = equipoDao.buscarPorId(equipoId);
            if (equipo != null) {
                equipo.cambiarEstado(Equipo.DANADO);
                equipoDao.actualizar(equipo);
            }
        }
    }

    public void actualizar(Recepcion recepcion) throws SQLException {
        validar(recepcion);
        if (recepcion.getId() <= 0) {
            throw new IllegalArgumentException("Seleccione la recepción que desea actualizar.");
        }
        dao.actualizar(recepcion);
        // Marcar los equipos dañados como DAÑADO (no se revierte automáticamente
        // para evitar perder el estado si los checkboxes no se cargaron bien)
        for (int equipoId : recepcion.getEquiposDanadosIds()) {
            Equipo equipo = equipoDao.buscarPorId(equipoId);
            if (equipo != null) {
                equipo.cambiarEstado(Equipo.DANADO);
                equipoDao.actualizar(equipo);
            }
        }
    }

    public Optional<Recepcion> buscarPorId(int id) throws SQLException {
        return Optional.ofNullable(dao.buscarPorId(id));
    }

    public Collection<Recepcion> listar() throws SQLException {
        return dao.listar();
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }

    private void validar(Recepcion r) throws SQLException {
        if (r == null) {
            throw new IllegalArgumentException("La recepción es obligatoria.");
        }
        if (r.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora de la recepción es obligatoria.");
        }

        Laboratorio laboratorio = laboratorioDao.buscarPorId(r.getLaboratorioId());
        if (laboratorio == null) {
            throw new IllegalArgumentException("Seleccione un laboratorio válido.");
        }

        Usuario docente = usuarioDao.buscarPorId(r.getDocenteId());
        if (docente == null) {
            throw new IllegalArgumentException("Seleccione un docente válido.");
        }
        if (!"Docente".equalsIgnoreCase(docente.getRol())) {
            throw new IllegalArgumentException("La recepción del laboratorio solo puede registrarla un docente.");
        }
    }
}
