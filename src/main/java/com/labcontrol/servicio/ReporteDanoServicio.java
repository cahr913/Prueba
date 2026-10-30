package com.labcontrol.servicio;

import com.labcontrol.dao.*;
import com.labcontrol.modelo.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ReporteDanoServicio {

    private final ReporteDanoDao dao = new ReporteDanoDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final FotoEvidenciaDao fotoDao = new FotoEvidenciaDao();

    public void registrar(ReporteDano reporte) throws SQLException {
        validar(reporte);
        dao.insertar(reporte);

        Equipo equipo = equipoDao.buscarPorId(reporte.getEquipoid());
        if (equipo != null) {
            equipo.cambiarEstado(Equipo.DANADO);
            equipoDao.actualizar(equipo);
        }

        guardarFotos(reporte);
    }

    public void actualizar(ReporteDano reporte) throws SQLException {
        validar(reporte);
        if (reporte.getId() <= 0) {
            throw new IllegalArgumentException("Seleccione el reporte que desea actualizar.");
        }
        // Equipo anterior (para revertir si cambió)
        ReporteDano anterior = dao.buscarPorId(reporte.getId());
        int equipoAnterior = (anterior != null) ? anterior.getEquipoid() : 0;

        dao.actualizar(reporte);
        guardarFotos(reporte);

        // Actualizar estado del equipo nuevo → DAÑADO
        Equipo equipoNuevo = equipoDao.buscarPorId(reporte.getEquipoid());
        if (equipoNuevo != null) {
            equipoNuevo.cambiarEstado(Equipo.DANADO);
            equipoDao.actualizar(equipoNuevo);
        }
        // Revertir equipo anterior si cambió (solo si no tiene otro reporte activo)
        if (equipoAnterior > 0 && equipoAnterior != reporte.getEquipoid()) {
            Equipo eqAnt = equipoDao.buscarPorId(equipoAnterior);
            if (eqAnt != null && Equipo.DANADO.equalsIgnoreCase(eqAnt.getEstado())) {
                eqAnt.cambiarEstado(Equipo.ACTIVO);
                equipoDao.actualizar(eqAnt);
            }
        }
    }

    public Optional<ReporteDano> buscarPorId(int id) throws SQLException {
        ReporteDano reporte = dao.buscarPorId(id);
        if (reporte != null) {
            reporte.getFotos().addAll(fotoDao.listarPorReporte(id));
        }
        return Optional.ofNullable(reporte);
    }

    public Collection<ReporteDano> listar() throws SQLException {
        return dao.listar();
    }

    
    public void eliminar(int id) throws SQLException {
        // Obtener el reporte antes de eliminarlo (para saber qué equipo revertir)
        ReporteDano reporte = dao.buscarPorId(id);
        int equipoId = (reporte != null) ? reporte.getEquipoid() : 0;

        dao.eliminar(id);

        // Revertir el estado del equipo a ACTIVO (si no tiene otro reporte activo)
        if (equipoId > 0) {
            Equipo equipo = equipoDao.buscarPorId(equipoId);
            if (equipo != null && Equipo.DANADO.equalsIgnoreCase(equipo.getEstado())) {
                equipo.cambiarEstado(Equipo.ACTIVO);
                equipoDao.actualizar(equipo);
            }
        }
    }

    
    public List<FotoEvidencia> fotosDeReporte(int reporteId) throws SQLException {
        return fotoDao.listarPorReporte(reporteId);
    }

    private void validar(ReporteDano r) throws SQLException {
        if (r == null) {
            throw new IllegalArgumentException("El reporte de daño es obligatorio.");
        }
        if (r.getTipoFalla() == null || r.getTipoFalla().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de falla es obligatorio.");
        }
        if (r.getDescripcion() == null || r.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del problema es obligatoria.");
        }
        if (r.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora del reporte es obligatoria.");
        }

        Equipo equipo = equipoDao.buscarPorId(r.getEquipoid());
        if (equipo == null) {
            throw new IllegalArgumentException("Seleccione un equipo válido.");
        }
        if (r.getLaboratorioid() <= 0) {
            r.setLaboratorioid(equipo.getLaboratorioId());
        }

        Usuario docente = usuarioDao.buscarPorId(r.getDocenteid());
        if (docente == null) {
            throw new IllegalArgumentException("Seleccione un docente válido.");
        }
    }

    private void guardarFotos(ReporteDano reporte) throws SQLException {
        if (reporte.getId() > 0) {
            try (java.sql.Connection cn = com.labcontrol.db.ConexionBD.abrir();
                 java.sql.PreparedStatement ps = cn.prepareStatement(
                         "DELETE FROM foto_evidencia WHERE reporte_dano_id = ?")) {
                ps.setInt(1, reporte.getId());
                ps.executeUpdate();
            }
        }
        for (FotoEvidencia f : reporte.getFotos()) {
            if (f.getRutaArchivo() == null || f.getRutaArchivo().trim().isEmpty()) {
                continue;
            }
            if (!FotoEvidencia.validarFormato(f.getRutaArchivo())) {
                throw new IllegalArgumentException(
                        "La ruta de la fotografía no corresponde a una imagen válida: " + f.getRutaArchivo());
            }
            f.setReporteid(reporte.getId());
            if (f.getEquipoid() <= 0) {
                f.setEquipoid(reporte.getEquipoid());
            }
            fotoDao.insertar(f);
        }
    }
}
