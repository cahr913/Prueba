package com.labcontrol.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {
    private int id;
    private String codigo;
    private int docenteId;
    private int laboratorioId;
    private String curso;
    private String actividad;
    private String dia;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String observaciones;
    private Laboratorio laboratorio;
    private Usuario usuario;

    public Horario() {
    }

    public Horario(String codigo, Laboratorio laboratorio, String dia, LocalTime horaInicio, LocalTime horaFin,
            String actividad, Usuario usuario) {
        this.codigo = codigo;
        this.laboratorio = laboratorio;
        if (laboratorio != null) {
            this.laboratorioId = laboratorio.getId();
        }
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.actividad = actividad;
        this.curso = actividad;
        this.usuario = usuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getDocenteId() { return docenteId; }
    public void setDocenteId(int docenteId) { this.docenteId = docenteId; }

    public int getLaboratorioId() {
        if (laboratorio != null && laboratorio.getId() != 0) {
            return laboratorio.getId();
        }
        return laboratorioId;
    }

    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    public String getCurso() { return curso != null ? curso : actividad; }
    public void setCurso(String curso) {
        this.curso = curso;
        if (this.actividad == null) {
            this.actividad = curso;
        }
    }

    public String getActividad() { return actividad != null ? actividad : curso; }
    public void setActividad(String actividad) {
        this.actividad = actividad;
        this.curso = actividad;
    }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Laboratorio getLaboratorio() { return laboratorio; }
    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
        if (laboratorio != null && laboratorio.getId() != 0) {
            this.laboratorioId = laboratorio.getId();
        }
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
