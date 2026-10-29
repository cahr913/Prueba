package com.labcontrol.modelo;

import java.time.LocalDateTime;

public class Recepcion {
    private int id;
    private int laboratorioId;
    private int docenteId;
    private int horarioId;
    private int idEquipo;
    private LocalDateTime fechaHora;
    private String estado;
    private String estadoGeneral;
    private String limpieza;
    private String estadoMobiliario;
    private boolean energiaElectrica;
    private boolean red;
    private String estadoEquipos;
    private String observaciones;

    public Recepcion() {
    }

    public Recepcion(int idRecepcion, int idEquipo, int idDocente, LocalDateTime fechaHora,
                     String estado, String observaciones) {
        if (idRecepcion <= 0 || idEquipo <= 0 || idDocente <= 0) {
            throw new IllegalArgumentException("Los identificadores deben ser positivos.");
        }
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }
        this.id = idRecepcion;
        this.idEquipo = idEquipo;
        this.docenteId = idDocente;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.estadoGeneral = estado;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdRecepcion() { return id; }

    public int getLaboratorioId() { return laboratorioId; }
    public void setLaboratorioId(int laboratorioId) { this.laboratorioId = laboratorioId; }

    public int getDocenteId() { return docenteId; }
    public void setDocenteId(int docenteId) { this.docenteId = docenteId; }

    public int getIdDocente() { return docenteId; }

    public int getHorarioId() { return horarioId; }
    public void setHorarioId(int horarioId) { this.horarioId = horarioId; }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado != null ? estado : estadoGeneral; }
    public void setEstado(String estado) {
        this.estado = estado;
        this.estadoGeneral = estado;
    }

    public String getEstadoGeneral() { return estadoGeneral != null ? estadoGeneral : estado; }
    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
        if (this.estado == null) {
            this.estado = estadoGeneral;
        }
    }

    public String getLimpieza() { return limpieza; }
    public void setLimpieza(String limpieza) { this.limpieza = limpieza; }

    public String getEstadoMobiliario() { return estadoMobiliario; }
    public void setEstadoMobiliario(String estadoMobiliario) { this.estadoMobiliario = estadoMobiliario; }

    public boolean isEnergiaElectrica() { return energiaElectrica; }
    public void setEnergiaElectrica(boolean energiaElectrica) { this.energiaElectrica = energiaElectrica; }

    public boolean isRed() { return red; }
    public void setRed(boolean red) { this.red = red; }

    public String getEstadoEquipos() { return estadoEquipos; }
    public void setEstadoEquipos(String estadoEquipos) { this.estadoEquipos = estadoEquipos; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Recepción #" + id + " | Equipo: " + idEquipo + " | Docente: " + docenteId + " | Estado: " + getEstado();
    }
}
