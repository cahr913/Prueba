package com.labcontrol.modelo;

public class Instalacion {
    private int id;
    private int equipoId;
    private int softwareId;
    private String fechaInstalacion;

    public Instalacion() {
    }

    public Instalacion(int id, int equipoId, int softwareId, String fechaInstalacion) {
        this.id = id;
        this.equipoId = equipoId;
        this.softwareId = softwareId;
        this.fechaInstalacion = fechaInstalacion;
    }

    public boolean validarLicencia() {
        return equipoId > 0
                && softwareId > 0
                && fechaInstalacion != null
                && !fechaInstalacion.trim().isEmpty();
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public int getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(int softwareId) {
        this.softwareId = softwareId;
    }

    public String getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(String fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    @Override
    public String toString() {
        return "Instalacion{id=" + id + ", equipoId=" + equipoId
                + ", softwareId=" + softwareId + ", fechaInstalacion='" + fechaInstalacion + "'}";
    }
}
