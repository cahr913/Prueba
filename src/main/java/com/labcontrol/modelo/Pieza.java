package com.labcontrol.modelo;

public class Pieza {
    public enum TipoOperacion {
        REPARACION,
        REEMPLAZO
    }

    private int id;
    private int mantenimientoId;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private Accion accion;
    private String observaciones;

    public Pieza() {
    }

    public Pieza(int idPieza, String nombre, int cantidad, TipoOperacion tipoOperacion) {
        if (idPieza <= 0) {
            throw new IllegalArgumentException("El ID de la pieza debe ser positivo.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la pieza es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (tipoOperacion == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de operación.");
        }
        this.id = idPieza;
        this.nombre = nombre.trim();
        this.cantidad = cantidad;
        this.accion = Accion.valueOf(tipoOperacion.name());
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPieza() {
        return id;
    }

    public int getMantenimientoId() {
        return mantenimientoId;
    }

    public void setMantenimientoId(int mantenimientoId) {
        this.mantenimientoId = mantenimientoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public TipoOperacion getTipoOperacion() {
        return accion == null ? null : TipoOperacion.valueOf(accion.name());
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        if (tipoOperacion == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de operación.");
        }
        this.accion = Accion.valueOf(tipoOperacion.name());
    }

    public String getObservaciones() {

        return observaciones;

    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Pieza #" + id + " | Nombre: " + nombre + " | Cantidad: " + cantidad + " | Operación: " + getTipoOperacion();
    }
}
