package com.labcontrol.dao;

import java.util.ArrayList;
import java.util.List;

public class ResultadoReporte {

    private final String titulo;
    private final String[] encabezados;
    private final List<Object[]> filas;

    public ResultadoReporte(String titulo, String[] encabezados) {
        this.titulo = titulo;
        this.encabezados = encabezados;
        this.filas = new ArrayList<>();
    }

    public String getTitulo() { return titulo; }
    public String[] getEncabezados() { return encabezados; }
    public List<Object[]> getFilas() { return filas; }

    public void agregarFila(Object... valores) {
        filas.add(valores);
    }

    public boolean estaVacio() {
        return filas.isEmpty();
    }
}
