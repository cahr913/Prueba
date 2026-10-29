package com.labcontrol.legacy.repositorio;

import com.labcontrol.legacy.repositorio.archivo.*;
import java.nio.file.Path;

/**
 * Único lugar donde se decide DÓNDE se guardan los datos.
 * Hoy: archivos. Al conectar la base de datos, solo se cambian las 4 líneas de inicializar()
 * por las implementaciones JDBC; los formularios no se tocan.
 */
public final class Repositorios {
    private static LaboratorioRepositorio laboratorios;
    private static EquipoRepositorio equipos;
    private static SoftwareRepositorio software;
    private static InstalacionRepositorio instalaciones;

    private Repositorios() { }

    public static synchronized void inicializar() {
        if (laboratorios != null) return;
        laboratorios = new LaboratorioArchivo();
        equipos = new EquipoArchivo();
        software = new SoftwareArchivo();
        instalaciones = new InstalacionArchivo();
    }

    public static LaboratorioRepositorio laboratorios() { inicializar(); return laboratorios; }
    public static EquipoRepositorio equipos() { inicializar(); return equipos; }
    public static SoftwareRepositorio software() { inicializar(); return software; }
    public static InstalacionRepositorio instalaciones() { inicializar(); return instalaciones; }

    /** Carpeta donde se guardan los archivos de datos. */
    public static Path carpetaDatos() { return ArchivoTsv.carpetaDatos(); }
}

