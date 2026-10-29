package com.labcontrol.servicio;

import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.FotoEvidenciaDao;
import com.labcontrol.dao.ReporteDanoDao;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.FotoEvidencia;
import com.labcontrol.modelo.ReporteDano;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class ReporteDanoService {

    private final ReporteDanoDao dao = new ReporteDanoDao();

    public void registrar(ReporteDano reporte) throws SQLException {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte de daño es obligatorio.");
        }
        dao.insertar(reporte);

        // Actualizar estado del equipo a DAÑADO (según el Problema No. 1)
        EquipoDao equipoDao = new EquipoDao();
        Equipo equipo = equipoDao.buscarPorId(reporte.getEquipoid());
        if (equipo != null) {
            equipo.cambiarEstado(Equipo.DANADO);
            equipoDao.actualizar(equipo);
        }

        // Persistir las fotos de evidencia asociadas al reporte.
        if (reporte.getFotos() != null && !reporte.getFotos().isEmpty()) {
            FotoEvidenciaDao fotoDao = new FotoEvidenciaDao();
            int reporteId = reporte.getId();
            for (FotoEvidencia f : reporte.getFotos()) {
                f.setReporteid(reporteId);
                fotoDao.insertar(f);
            }
        }
    }

    public Optional<ReporteDano> buscarPorId(int id) throws SQLException {
        return Optional.ofNullable(dao.buscarPorId(id));
    }

    public Collection<ReporteDano> listar() throws SQLException {
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
