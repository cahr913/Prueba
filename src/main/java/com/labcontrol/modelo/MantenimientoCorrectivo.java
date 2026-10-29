package com.labcontrol.modelo;

public class MantenimientoCorrectivo extends Mantenimiento {

    public MantenimientoCorrectivo(String numeroServicio, int equipoId, int reporteId, int responsableId,
                                   String tipo, String diagnostico, String estadoFinal) {
        super(numeroServicio, equipoId, reporteId, responsableId, tipo, diagnostico, estadoFinal);
    }

    public MantenimientoCorrectivo() {
    }

    @Override
    public void agregarPieza(Pieza pieza) {
        super.agregarPieza(pieza);
    }

    public void finalizar() {
        if (getPiezas().isEmpty()) {
            throw new IllegalStateException(
                "No se puede finalizar un mantenimiento correctivo sin piezas asignadas.");
        }
        setEstado(Estado.FINALIZADO);
    }
}
