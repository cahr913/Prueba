package com.labcontrol.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Mantenimiento {

    public enum Tipo {
        PREVENTIVO,
        CORRECTIVO
    }

    public enum Estado {
        PENDIENTE,
        EN_PROCESO,
        FINALIZADO,
        CANCELADO
    }

    private int id;
    private String numeroServicio;
    private int equipoId;
    private int reporteDanoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaInicioCompleta;
    private int responsableId;
    private String tipoTexto;
    private Tipo tipo;
    private Estado estado = Estado.PENDIENTE;
    private String fallaReportada;
    private String diagnostico;
    private String actividadesRealizadas;
    private String resultado;
    private String estadoFinalEquipo;
    private String recomendaciones;
    private String observaciones;
    private String descripcion;
    private List<Pieza> piezas = new ArrayList<>();

    public Mantenimiento() {
    }

    public Mantenimiento(int idMantenimiento, String numeroServicio, int idEquipo, int idTecnico,
                         Tipo tipo, Estado estado, LocalDateTime fechaInicio, String descripcion) {
        if (idMantenimiento <= 0 || idEquipo <= 0 || idTecnico <= 0) {
            throw new IllegalArgumentException("Los identificadores deben ser positivos.");
        }
        if (numeroServicio == null || numeroServicio.trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de servicio es obligatorio.");
        }
        if (tipo == null || estado == null || fechaInicio == null) {
            throw new IllegalArgumentException("El tipo, el estado y la fecha son obligatorios.");
        }
        this.id = idMantenimiento;
        this.numeroServicio = numeroServicio.trim();
        this.equipoId = idEquipo;
        this.responsableId = idTecnico;
        this.tipo = tipo;
        this.tipoTexto = tipo.name();
        this.estado = estado;
        this.fechaInicioCompleta = fechaInicio;
        this.fechaInicio = fechaInicio.toLocalDate();
        this.descripcion = descripcion;
    }

    public Mantenimiento(String numeroServicio, int equipoId, int reporteId, int responsableId,
                         String tipo, String diagnostico, String estadoFinal) {
        this.numeroServicio = numeroServicio;
        this.equipoId = equipoId;
        this.reporteDanoId = reporteId;
        this.responsableId = responsableId;
        setTipoTexto(tipo);
        this.diagnostico = diagnostico;
        this.estadoFinalEquipo = estadoFinal;
    }

    public int getId() {

        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMantenimiento() {
        return id;
    }

    public String getNumeroServicio() {
        return numeroServicio;
    }

    public void setNumeroServicio(String numeroServicio) {
        this.numeroServicio = numeroServicio;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public int getIdEquipo() {
        return equipoId;
    }

    public int getReporteDanoId() {
        return reporteDanoId;
    }

    public void setReporteDanoId(int reporteDanoId) {
        this.reporteDanoId = reporteDanoId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaInicioCompleta() {
        return fechaInicioCompleta;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
    }

    public int getIdTecnico() {
        return responsableId;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
        if (tipo != null) {
            this.tipoTexto = tipo.name();
        }
    }

    public String getTipoTexto() {

        return tipoTexto;

    }

    public void setTipoTexto(String tipoTexto) {
        this.tipoTexto = tipoTexto;
        this.tipo = parseTipo(tipoTexto);
    }

    public String tipoValorBd() {
        return tipo == Tipo.CORRECTIVO ? "Correctivo" : "Preventivo";
    }

    public Estado getEstado() {

        return estado;

    }

    public void setEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = estado;
    }

    public String getEstadoTexto() {

        return estado != null ? estado.name() : null;

    }

    public void setEstadoTexto(String estadoTexto) {
        if (estadoTexto == null || estadoTexto.isBlank()) {
            this.estado = Estado.PENDIENTE;
            return;
        }
        try {
            this.estado = Estado.valueOf(estadoTexto.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            this.estado = Estado.PENDIENTE;
        }
    }

    public void finalizar() {
        if (estado == Estado.FINALIZADO) {
            throw new IllegalStateException("El mantenimiento ya esta finalizado.");
        }
        this.estado = Estado.FINALIZADO;
    }

    public double calcularCostoEstimado() {
        return 0.0;
    }

    public String getFallaReportada() {

        return fallaReportada;

    }

    public void setFallaReportada(String fallaReportada) {
        this.fallaReportada = fallaReportada;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getActividadesRealizadas() {
        return actividadesRealizadas;
    }

    public void setActividadesRealizadas(String actividadesRealizadas) {
        this.actividadesRealizadas = actividadesRealizadas;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEstadoFinalEquipo() {
        return estadoFinalEquipo;
    }

    public void setEstadoFinalEquipo(String estadoFinalEquipo) {
        this.estadoFinalEquipo = estadoFinalEquipo;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Pieza> getPiezas() {
        return piezas;
    }

    public Pieza getPieza() {
        return piezas.isEmpty() ? null : piezas.get(0);
    }

    public void agregarPieza(Pieza pieza) {
        if (pieza == null) {
            throw new IllegalArgumentException("La pieza es obligatoria.");
        }
        this.piezas.add(pieza);
    }

    private static Tipo parseTipo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Tipo.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            if (valor.equalsIgnoreCase("Preventivo")) {
                return Tipo.PREVENTIVO;
            }
            if (valor.equalsIgnoreCase("Correctivo")) {
                return Tipo.CORRECTIVO;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "Mantenimiento{" + "id=" + id + ", numeroServicio=" + numeroServicio
                + ", equipoId=" + equipoId + ", tipo=" + tipo + ", estado=" + estado + "}";
    }
}
