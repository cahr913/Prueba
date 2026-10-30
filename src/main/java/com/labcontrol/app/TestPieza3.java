package com.labcontrol.app;
import com.labcontrol.servicio.MantenimientoServicio;
import com.labcontrol.modelo.Mantenimiento;
public class TestPieza3 {
    public static void main(String[] args) throws Exception {
        MantenimientoServicio servicio = new MantenimientoServicio();
        Mantenimiento m = servicio.buscarPorId(1).orElse(null);
        if (m == null) {
            System.out.println("Mantenimiento no encontrado");
        } else {
            System.out.println("Mantenimiento: " + m.getNumeroServicio());
            System.out.println("Piezas: " + m.getPiezas().size());
            for (com.labcontrol.modelo.Pieza p : m.getPiezas()) {
                System.out.println("  " + p.getNombre() + " | " + p.getCantidad() + " | " + p.getAccion());
            }
        }
    }
}
