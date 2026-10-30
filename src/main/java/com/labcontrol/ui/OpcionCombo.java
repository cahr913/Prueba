package com.labcontrol.UI;

public class OpcionCombo {

    private final int id;
    private final String etiqueta;

    public OpcionCombo(int id, String etiqueta) {
        this.id = id;
        this.etiqueta = etiqueta;
    }

    public int getId() { return id; }
    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() {
        return etiqueta;
    }
}
