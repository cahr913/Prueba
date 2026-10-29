package com.labcontrol.legacy.repositorio;

import com.labcontrol.modelo.Computadora;
import com.labcontrol.modelo.Equipo;
import java.util.List;

public interface EquipoRepositorio {
    /** Todos los equipos (computadoras incluidas). */
    List<Equipo> listar();
    List<Computadora> listarComputadoras();
    /** id == 0 inserta (y asigna el id); id > 0 actualiza. */
    void guardar(Equipo equipo);
    boolean existeCodigo(String codigoInventario, int exceptoId);
}

