package com.labcontrol.modelo;

public class Tecnico extends Usuario {
    private int idTecnico;
    private String especialidad;
    private String telefono;

    public int getIdTecnico() { return idTecnico; }
    public void setIdTecnico(int idTecnico) { this.idTecnico = idTecnico; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void consultarDanos() {}
    public void registrarMantenimiento() {}
    public void actualizarEstadoEquipo() {}
    public void generarReporteServicio() {}
}
