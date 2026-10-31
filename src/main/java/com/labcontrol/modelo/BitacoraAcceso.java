package com.labcontrol.modelo;

import java.time.LocalDateTime;

public class BitacoraAcceso {
    private int idBitacora;
    private LocalDateTime fechaHora;
    private String accion;
    private String ip;
    private String dispositivo;

    public BitacoraAcceso(int idBitacora, LocalDateTime fechaHora, String accion, String ip, String dispositivo) {
        this.idBitacora = idBitacora;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.ip = ip;
        this.dispositivo = dispositivo;
    }

    public BitacoraAcceso() {
    }
    
    public int getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(int idBitacora) {
        this.idBitacora = idBitacora;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }
    
    
    public void registrarAcceso() {}
    public void consultarBitacora() {}
    public void eliminarRegistro() {}
}
