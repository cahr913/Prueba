package com.labcontrol.modelo;

import java.util.ArrayList;

public class FotoEvidencia {
    private int id;
    private int reporteid;
    private int equipoid;
    private String rutaArchivo;
    private String descripcion;

    public FotoEvidencia() {
    }

    public FotoEvidencia(int id, int reporteid, int equipoid, String rutaArchivo, String descripcion) {
        this.id = id;
        this.reporteid = reporteid;
        this.equipoid = equipoid;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReporteid() { return reporteid; }
    public void setReporteid(int reporteid) { this.reporteid = reporteid; }

    public int getEquipoid() { return equipoid; }
    public void setEquipoid(int equipoid) { this.equipoid = equipoid; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public static final String[] EXTENSIONES_VALIDAS = { ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp" };

    public static boolean validarFormato(String img) {
        if (img == null) return false;
        String ruta = img.trim().toLowerCase();
        if (ruta.startsWith("http://") || ruta.startsWith("https://")) {
            return true;
        }
        for (String ext : EXTENSIONES_VALIDAS) {
            if (ruta.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}

