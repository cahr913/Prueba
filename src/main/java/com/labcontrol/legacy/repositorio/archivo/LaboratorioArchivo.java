package com.labcontrol.legacy.repositorio.archivo;

import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.legacy.repositorio.LaboratorioRepositorio;
import com.labcontrol.legacy.repositorio.RepositorioException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaboratorioArchivo implements LaboratorioRepositorio {
    private final ArchivoTsv archivo = new ArchivoTsv("laboratorios.tsv");
    private final List<Laboratorio> labs = new ArrayList<>();

    public LaboratorioArchivo() {
        for (String[] f : archivo.leer()) {
            int id = f.length >= 3 ? ArchivoTsv.entero(f[0]) : 0;
            if (id > 0) labs.add(new Laboratorio(id, f[1], f[2]));
        }
    }

    @Override public List<Laboratorio> listar() { return new ArrayList<>(labs); }

    @Override public Laboratorio buscarPorId(int id) {
        for (Laboratorio l : labs) if (l.getId() == id) return l;
        return null;
    }

    @Override public Laboratorio obtenerOCrear(String codigo) {
        String c = codigo.trim().toUpperCase(Locale.ROOT);
        for (Laboratorio l : labs) if (l.getCodigo().equalsIgnoreCase(c)) return l;
        int max = 0;
        for (Laboratorio l : labs) max = Math.max(max, l.getId());
        Laboratorio nuevo = new Laboratorio(max + 1, c, c);
        labs.add(nuevo);
        try {
            persistir();
        } catch (RepositorioException e) {
            labs.remove(nuevo);
            throw e;
        }
        return nuevo;
    }

    private void persistir() {
        List<String[]> filas = new ArrayList<>();
        for (Laboratorio l : labs)
            filas.add(new String[] { String.valueOf(l.getId()), l.getCodigo(), l.getNombre() });
        archivo.escribir(filas);
    }
}

