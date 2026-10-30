package com.labcontrol.modelo;

public class Software {
    
    public static final String[] TIPOS = { "Sistema", "Aplicacion" };
    public static final String[] LICENCIAS = { "Open Source", "Comercial", "Freeware" };
    public static final String[] ESTADOS = { "Activo", "Inactivo" };

    private int id;
    private String nombre;
    private String version;
    private String tipo;
    private String tipoLicencia;
    private String estado = "Activo";

    public Software() {
    }

    public Software(int id, String nombre, String version, String tipo, String tipoLicencia, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.version = version;
        this.tipo = tipo;
        this.tipoLicencia = tipoLicencia;
        this.estado = estado;
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public String getLicencia() {
        return tipoLicencia;
    }

    public void setLicencia(String licencia) {
        this.tipoLicencia = licencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
