package com.labcontrol.modelo;

public class Docente extends Usuario {
    private int idDocente;
    private String especialidad;
    private String telefono;

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void consultarHorarios() {}
    public void registrarRecepcion() {}
    public void consultarLaboratorio() {}
    public void reportarDano() {}
}
