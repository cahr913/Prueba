package com.labcontrol.modelo;

public class Equipo {
    
    public static final String ACTIVO = "ACTIVO";
    public static final String DANADO = "DAÑADO";
    public static final String MANTENIMIENTO = "EN MANTENIMIENTO";
    public static final String FUERA_DE_SERVICIO = "FUERA DE SERVICIO";
    public static final String[] ESTADOS = { ACTIVO, DANADO, MANTENIMIENTO, FUERA_DE_SERVICIO };
    public static final String[] TIPOS_OTROS = { "Impresora", "Proyector", "Switch", "Router", "Otro" };


    private int id;
    private String codigoInventario;
    private String tipo;
    private String numSerie;
    private String marca;
    private String modelo;
    private String fechaAdquisicion;
    private String estado = ACTIVO;
    private int laboratorioId;
    private String observaciones;

    public Equipo() {
    }

    public Equipo(int id, String codigoInventario, String tipo, String numSerie,
                  String marca, String modelo, String fechaAdquisicion,
                  String estado, int laboratorioId) {
        this(id, codigoInventario, tipo, numSerie, marca, modelo, fechaAdquisicion, estado, laboratorioId, null);
    }

    public Equipo(int id, String codigoInventario, String tipo, String numSerie,
                  String marca, String modelo, String fechaAdquisicion,
                  String estado, int laboratorioId, String observaciones) {
        this.id = id;
        this.codigoInventario = codigoInventario;
        this.tipo = tipo;
        this.numSerie = numSerie;
        this.marca = marca;
        this.modelo = modelo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.estado = estado;
        this.laboratorioId = laboratorioId;
        this.observaciones = observaciones;
    }

    public void cambiarEstado(String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del equipo es obligatorio.");
        }
        this.estado = nuevoEstado.trim().toUpperCase();
    }

    public boolean estaDisponible() {
        return ACTIVO.equalsIgnoreCase(estado) || "Activo".equalsIgnoreCase(estado);
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(String fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getLaboratorioId() {
        return laboratorioId;
    }

    public void setLaboratorioId(int laboratorioId) {
        this.laboratorioId = laboratorioId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
