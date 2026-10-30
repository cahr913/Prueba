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

    
    @Override
    public void finalizar() {
        if (getPiezas().isEmpty()) {
            throw new IllegalStateException(
                "No se puede finalizar un mantenimiento correctivo sin piezas asignadas.");
        }
        super.finalizar();
    }

    
    @Override
    public double calcularCostoEstimado() {
        double total = 0.0;
        for (Pieza p : getPiezas()) {
            total += p.getCantidad() * 50.0;
        }
        return total;
    }
}
