package com.labcontrol.legacy.repositorio;

import com.labcontrol.modelo.Software;
import java.util.List;

public interface SoftwareRepositorio {
    List<Software> listar();
    /** id == 0 inserta (y asigna el id); id > 0 actualiza. */
    void guardar(Software software);
    boolean existe(String nombre, String version, int exceptoId);
}

