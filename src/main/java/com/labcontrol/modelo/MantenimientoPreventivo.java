package com.labcontrol.modelo;

public class MantenimientoPreventivo extends Mantenimiento {
    private String listaChequeo;

    public MantenimientoPreventivo() {
    }

    public MantenimientoPreventivo(String listaChequeo, String numeroServicio, int equipoId, int reporteId,
                                   int responsableId, String tipo, String diagnostico, String estadoFinal) {
        super(numeroServicio, equipoId, reporteId, responsableId, tipo, diagnostico, estadoFinal);
        this.listaChequeo = listaChequeo;
    }

    public String getListaChequeo() { return listaChequeo; }
    public void setListaChequeo(String listaChequeo) { this.listaChequeo = listaChequeo; }

    public void finalizar() {
        setEstado(Estado.FINALIZADO);
    }
}
