package com.labcontrol.modelo;

public class CentroEducativo {
    private int id;
    private String nombre;
    private String codigoInstitucional;
    private String direccion;
    private String telefono;
    private String correo;
    private String director;
    private String logoPath;

    public CentroEducativo() {
    }

    public CentroEducativo(String codigo, String nombre, String direccion, String telefono) {
        this.codigoInstitucional = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoInstitucional() { return codigoInstitucional; }
    public void setCodigoInstitucional(String codigoInstitucional) { this.codigoInstitucional = codigoInstitucional; }

    public String getCodigo() { return codigoInstitucional; }
    public void setCodigo(String codigo) { this.codigoInstitucional = codigo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    @Override
    public String toString() {
        return nombre;
    }
}
