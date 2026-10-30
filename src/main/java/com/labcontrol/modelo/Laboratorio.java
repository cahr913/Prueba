package com.labcontrol.modelo;

public class Laboratorio {
    private int id;
    private String codigo;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private String estado;
    private String responsable;
    private String observaciones;
    private int centroId;
    private CentroEducativo centro;

    public Laboratorio() {
    }

    public Laboratorio(int id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Laboratorio(String codigo, String nombre, int capacidad, String ubicacion, String estado,
            CentroEducativo centro) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.estado = estado;
        setCentro(centro);
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getCentroId() {
        if (centro != null && centro.getId() != 0) {
            return centro.getId();
        }
        return centroId;
    }

    public void setCentroId(int centroId) {

        this.centroId = centroId;

    }

    public CentroEducativo getCentro() {
        return centro;
    }

    public void setCentro(CentroEducativo centro) {
        this.centro = centro;
        if (centro != null && centro.getId() != 0) {
            this.centroId = centro.getId();
        }
    }

    @Override
    public String toString() {
        if (centro != null && centro.getNombre() != null) {
            return nombre + " (" + centro.getNombre() + ")";
        }
        return nombre != null ? nombre : codigo;
    }
}
