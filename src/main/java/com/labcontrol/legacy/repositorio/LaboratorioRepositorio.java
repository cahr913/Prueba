package com.labcontrol.legacy.repositorio;

import com.labcontrol.modelo.Laboratorio;
import java.util.List;

public interface LaboratorioRepositorio {
    List<Laboratorio> listar();
    Laboratorio buscarPorId(int id);
    /** Devuelve el laboratorio con ese código; si no existe, lo crea. */
    Laboratorio obtenerOCrear(String codigo);
}

