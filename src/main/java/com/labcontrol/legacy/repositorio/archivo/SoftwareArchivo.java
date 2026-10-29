package com.labcontrol.legacy.repositorio.archivo;

import com.labcontrol.modelo.Software;
import com.labcontrol.legacy.repositorio.RepositorioException;
import com.labcontrol.legacy.repositorio.SoftwareRepositorio;
import java.util.ArrayList;
import java.util.List;

/** software.tsv — id, nombre, version, tipo, licencia, estado */
public class SoftwareArchivo implements SoftwareRepositorio {
    private final ArchivoTsv archivo = new ArchivoTsv("software.tsv");
    private final List<Software> lista = new ArrayList<>();

    public SoftwareArchivo() {
        for (String[] f : archivo.leer()) {
            int id = f.length >= 6 ? ArchivoTsv.entero(f[0]) : 0;
            if (id > 0) lista.add(new Software(id, f[1], f[2], f[3], f[4], f[5]));
        }
    }

    @Override public List<Software> listar() { return new ArrayList<>(lista); }

    @Override public void guardar(Software s) {
        List<Software> respaldo = new ArrayList<>(lista);
        int idOriginal = s.getId();
        try {
            int pos = -1;
            for (int i = 0; i < lista.size(); i++) if (lista.get(i).getId() == idOriginal) pos = i;
            if (idOriginal == 0) {
                int max = 0;
                for (Software x : lista) max = Math.max(max, x.getId());
                s.setId(max + 1);
            }
            if (pos >= 0 && idOriginal != 0) lista.set(pos, s); else lista.add(s);
            persistir();
        } catch (RepositorioException e) {
            lista.clear();
            lista.addAll(respaldo);
            s.setId(idOriginal);
            throw e;
        }
    }

    @Override public boolean existe(String nombre, String version, int exceptoId) {
        for (Software s : lista)
            if (s.getId() != exceptoId && s.getNombre().equalsIgnoreCase(nombre.trim())
                    && s.getVersion().equalsIgnoreCase(version.trim())) return true;
        return false;
    }

    private void persistir() {
        List<String[]> filas = new ArrayList<>();
        for (Software s : lista)
            filas.add(new String[] { String.valueOf(s.getId()), s.getNombre(), s.getVersion(),
                    s.getTipo(), s.getLicencia(), s.getEstado() });
        archivo.escribir(filas);
    }
}

