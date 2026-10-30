package com.labcontrol.app;
import com.labcontrol.dao.PiezaDao;
import com.labcontrol.modelo.Pieza;
public class TestPieza2 {
    public static void main(String[] args) throws Exception {
        PiezaDao dao = new PiezaDao();
        System.out.println("Piezas del mantenimiento 1: " + dao.listarPorMantenimiento(1).size());
        for (Pieza p : dao.listarPorMantenimiento(1)) {
            System.out.println("  " + p.getId() + " | " + p.getNombre() + " | " + p.getCantidad() + " | " + p.getAccion());
        }
    }
}
