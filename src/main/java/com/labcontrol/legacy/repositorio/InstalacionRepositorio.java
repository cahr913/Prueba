package com.labcontrol.legacy.repositorio;

import com.labcontrol.modelo.Instalacion;
import java.util.List;

public interface InstalacionRepositorio {
    List<Instalacion> listar();
    /** Inserta la instalación y le asigna su id. */
    void registrar(Instalacion instalacion);
    boolean existe(int equipoId, int softwareId);
}

