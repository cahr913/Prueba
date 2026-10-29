package com.labcontrol.legacy.repositorio.archivo;

import com.labcontrol.modelo.Instalacion;
import com.labcontrol.legacy.repositorio.InstalacionRepositorio;
import com.labcontrol.legacy.repositorio.RepositorioException;
import java.util.ArrayList;
import java.util.List;

/** instalaciones.tsv — id, equipoId, softwareId, fechaInstalacion */
public class InstalacionArchivo implements InstalacionRepositorio {
    private final ArchivoTsv archivo = new ArchivoTsv("instalaciones.tsv");
    private final List<Instalacion> lista = new ArrayList<>();

    public InstalacionArchivo() {
        for (String[] f : archivo.leer()) {
            int id = f.length >= 4 ? ArchivoTsv.entero(f[0]) : 0;
            if (id > 0) lista.add(new Instalacion(id, ArchivoTsv.entero(f[1]), ArchivoTsv.entero(f[2]), f[3]));
        }
    }

    @Override public List<Instalacion> listar() { return new ArrayList<>(lista); }

    @Override public void registrar(Instalacion i) {
        int idOriginal = i.getId();
        int max = 0;
        for (Instalacion x : lista) max = Math.max(max, x.getId());
        i.setId(max + 1);
        lista.add(i);
        try {
            persistir();
        } catch (RepositorioException e) {
            lista.remove(i);
            i.setId(idOriginal);
            throw e;
        }
    }

    @Override public boolean existe(int equipoId, int softwareId) {
        for (Instalacion i : lista)
            if (i.getEquipoId() == equipoId && i.getSoftwareId() == softwareId) return true;
        return false;
    }

    private void persistir() {
        List<String[]> filas = new ArrayList<>();
        for (Instalacion i : lista)
            filas.add(new String[] { String.valueOf(i.getId()), String.valueOf(i.getEquipoId()),
                    String.valueOf(i.getSoftwareId()), i.getFechaInstalacion() });
        archivo.escribir(filas);
    }
}

