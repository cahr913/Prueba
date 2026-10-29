package com.labcontrol.modelo;

public class Administrador extends Usuario {
    private int idAdmin;
    private String cargo;
    private String nivelAcceso;

    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(String nivelAcceso) { this.nivelAcceso = nivelAcceso; }

    public void gestionarUsuarios() {}
    public void gestionarLaboratorios() {}
    public void gestionarEquipos() {}
    public void generarReportes() {}
    public void configurarSistema() {}
}
