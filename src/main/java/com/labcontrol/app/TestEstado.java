package com.labcontrol.app;
import com.labcontrol.modelo.Mantenimiento;
import com.labcontrol.modelo.MantenimientoPreventivo;
import com.labcontrol.servicio.MantenimientoServicio;
public class TestEstado {
    public static void main(String[] args) throws Exception {
        MantenimientoServicio servicio = new MantenimientoServicio();
        Mantenimiento m = new MantenimientoPreventivo();
        m.setNumeroServicio("MS-TEST-ENPROCESO");
        m.setEquipoId(5);
        m.setResponsableId(4);
        m.setTipoTexto("Preventivo");
        m.setEstado(Mantenimiento.Estado.EN_PROCESO);
        m.setFechaInicio(java.time.LocalDate.now());
        m.setFallaReportada("Prueba");
        m.setDiagnostico("Diag");
        m.setActividadesRealizadas("Act");
        m.setResultado("OK");
        m.setEstadoFinalEquipo("ACTIVO");
        m.setRecomendaciones("Rec");
        m.setObservaciones("Obs");
        try {
            servicio.registrar(m);
            System.out.println("OK: guardado con estado " + m.getEstadoTexto());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
