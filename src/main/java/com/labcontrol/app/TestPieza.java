package com.labcontrol.app;
import com.labcontrol.servicio.PiezaServicio;
import com.labcontrol.modelo.Pieza;
public class TestPieza {
    public static void main(String[] args) throws Exception {
        PiezaServicio servicio = new PiezaServicio();
        System.out.println("Piezas: " + servicio.listar().size());
        for (Pieza p : servicio.listar()) {
            System.out.println("  " + p.getId() + " | " + p.getNombre() + " | " + p.getCantidad() + " | " + p.getAccion());
        }
    }
}
