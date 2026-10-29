package com.labcontrol.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteDano {
    private int id;
    private int laboratorioid;
    private int equipoid;
    private int docenteid;
    private int horarioid;
    private int idRecepcion;
    private String tipoFalla;
    private String descripcion;
    private LocalDateTime fechaHora;
    private String estado;
    private String estadoEncontrado;
    private String observaciones;
    private List<FotoEvidencia> fotos = new ArrayList<>();

    public ReporteDano() {
    }

    public ReporteDano(int idReporte, int idRecepcion, int idEquipo, int idDocente, String falla,
                       String descripcion, LocalDateTime fechaReporte, String estado) {
        
        if (idReporte <= 0 || idRecepcion <= 0 || idEquipo <= 0 || idDocente <= 0) {
            throw new IllegalArgumentException("Los identificadores deben ser positivos.");
        }
        if (falla == null || falla.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de falla es obligatorio.");
        }
        if (fechaReporte == null) {
            throw new IllegalArgumentException("La fecha del reporte es obligatoria.");
        }
        this.id = idReporte;
        this.idRecepcion = idRecepcion;
        this.equipoid = idEquipo;
        this.docenteid = idDocente;
        this.tipoFalla = falla;
        this.descripcion = descripcion;
        this.fechaHora = fechaReporte;
        this.estado = estado;
        this.estadoEncontrado = estado;
    }

    
    
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public int getIdReporte() { 
        return id; 
    }

    public int getLaboratorioid() { 
        return laboratorioid; 
    }
    
    public void setLaboratorioid(int laboratorioid) { 
        this.laboratorioid = laboratorioid; 
    }

    public int getEquipoid() { 
        return equipoid; 
    }
    
    public void setEquipoid(int equipoid) { 
        this.equipoid = equipoid; 
    }

    public int getIdEquipo() { 
        return equipoid; 
    }

    public int getDocenteid() { 
        return docenteid; 
    }
    
    public void setDocenteid(int docenteid) { 
        this.docenteid = docenteid; 
    }

    public int getIdDocente() { 
        return docenteid; 
    }

    public int getHorarioid() { 
        return horarioid; 
    }
    
    public void setHorarioid(int horarioid) { 
        this.horarioid = horarioid; 
    }

    public int getIdRecepcion() { 
        return idRecepcion; 
    }
    
    public void setIdRecepcion(int idRecepcion) { 
        this.idRecepcion = idRecepcion; 
    }
    
    public String getTipoFalla() { 
        return tipoFalla; 
    }
    
    public void setTipoFalla(String tipoFalla) { 
        this.tipoFalla = tipoFalla; 
    }

    public String getFalla() { 
        return tipoFalla; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public void actualizarDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public LocalDateTime getFechaHora() { 
        return fechaHora; 
    }
    
    public void setFechaHora(LocalDateTime fechaHora) { 
        this.fechaHora = fechaHora; 
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora == null || fechaHora.isBlank() ? null : LocalDateTime.parse(fechaHora);
    }

    public LocalDateTime getFechaReporte() { 
        return fechaHora; 
    }

    public String getEstado() { 
        return estado != null ? estado : estadoEncontrado; 
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
        this.estadoEncontrado = estado;
    }

    public String getEstadoEncontrado() { 
        return estadoEncontrado != null ? estadoEncontrado : estado; 
    }
    
    public void setEstadoEncontrado(String estadoEncontrado) { 
        this.estadoEncontrado = estadoEncontrado; 
    }

    public String getObservaciones() { 
        return observaciones;
    }
    public void setObservaciones(String observaciones) { 
        this.observaciones = observaciones; 
    }

    public List<FotoEvidencia> getFotos() { 
        return fotos; 
    }

    public void agregarFoto(FotoEvidencia foto) {
        if (foto == null) {
            throw new IllegalArgumentException("La foto de evidencia es obligatoria.");
        }
        this.fotos.add(foto);
    }

    @Override
    public String toString() {
        return "Reporte #" + id + " | Equipo: " + equipoid + " | Falla: " + tipoFalla + " | Estado: " + getEstado()
                + " | Fotos: " + fotos.size();
    }
}
