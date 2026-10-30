package com.labcontrol.modelo;

public class Computadora extends Equipo {
    private String hostname;
    private String ip;
    private String sistemaOperativo;
    private String arquitectura;
    private String caracteristicas;

    public Computadora() {
        setTipo("Computadora");
    }

    public Computadora(int id, String codigoInventario, String tipo, String numSerie,
                       String marca, String modelo, String fechaAdquisicion,
                       String estado, int laboratorioId, String hostname,
                       String ip, String sistemaOperativo, String arquitectura,
                       String caracteristicas) {
        super(id, codigoInventario, tipo, numSerie, marca, modelo, fechaAdquisicion, estado, laboratorioId);
        this.hostname = hostname;
        this.ip = ip;
        this.sistemaOperativo = sistemaOperativo;
        this.arquitectura = arquitectura;
        this.caracteristicas = caracteristicas;
    }

    public void instalarSoftware(Software software) {
        if (software != null && estaDisponible()) {
            System.out.println("Asignando el programa " + software.getNombre() + " al equipo " + hostname);
        }
    }

    public String getHostname() {

        return hostname;

    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getArquitectura() {
        return arquitectura;
    }

    public void setArquitectura(String arquitectura) {
        this.arquitectura = arquitectura;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}
