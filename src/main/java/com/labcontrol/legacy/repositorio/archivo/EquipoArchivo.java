package com.labcontrol.legacy.repositorio.archivo;

import com.labcontrol.modelo.Computadora;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.legacy.repositorio.EquipoRepositorio;
import com.labcontrol.legacy.repositorio.RepositorioException;
import java.util.ArrayList;
import java.util.List;

/**
 * equipos.tsv — una fila por equipo. Columnas:
 * id, tipo, codigoInventario, numSerie, marca, modelo, fechaAdquisicion, estado, laboratorioId,
 * hostname, ip, sistemaOperativo, arquitectura, caracteristicas   (las últimas 5 solo si es Computadora)
 */
public class EquipoArchivo implements EquipoRepositorio {
    private final ArchivoTsv archivo = new ArchivoTsv("equipos.tsv");
    private final List<Equipo> equipos = new ArrayList<>();

    public EquipoArchivo() {
        for (String[] f : archivo.leer()) {
            int id = f.length >= 14 ? ArchivoTsv.entero(f[0]) : 0;
            if (id <= 0) continue;
            Equipo e;
            if ("Computadora".equals(f[1])) {
                Computadora c = new Computadora();
                c.setHostname(f[9]);
                c.setIp(f[10]);
                c.setSistemaOperativo(f[11]);
                c.setArquitectura(f[12]);
                c.setCaracteristicas(f[13]);
                e = c;
            } else {
                e = new Equipo();
            }
            e.setId(id);
            e.setTipo(f[1]);
            e.setCodigoInventario(f[2]);
            e.setNumSerie(f[3]);
            e.setMarca(f[4]);
            e.setModelo(f[5]);
            e.setFechaAdquisicion(f[6]);
            e.setEstado(f[7]);
            e.setLaboratorioId(ArchivoTsv.entero(f[8]));
            equipos.add(e);
        }
    }

    @Override public List<Equipo> listar() { return new ArrayList<>(equipos); }

    @Override public List<Computadora> listarComputadoras() {
        List<Computadora> lista = new ArrayList<>();
        for (Equipo e : equipos) if (e instanceof Computadora c) lista.add(c);
        return lista;
    }

    @Override public void guardar(Equipo equipo) {
        List<Equipo> respaldo = new ArrayList<>(equipos);
        int idOriginal = equipo.getId();
        try {
            int pos = -1;
            for (int i = 0; i < equipos.size(); i++) if (equipos.get(i).getId() == idOriginal) pos = i;
            if (idOriginal == 0) {
                int max = 0;
                for (Equipo e : equipos) max = Math.max(max, e.getId());
                equipo.setId(max + 1);
            }
            if (pos >= 0 && idOriginal != 0) equipos.set(pos, equipo); else equipos.add(equipo);
            persistir();
        } catch (RepositorioException e) {
            equipos.clear();
            equipos.addAll(respaldo);
            equipo.setId(idOriginal);
            throw e;
        }
    }

    @Override public boolean existeCodigo(String codigo, int exceptoId) {
        for (Equipo e : equipos)
            if (e.getId() != exceptoId && e.getCodigoInventario().equalsIgnoreCase(codigo.trim())) return true;
        return false;
    }

    private void persistir() {
        List<String[]> filas = new ArrayList<>();
        for (Equipo e : equipos) {
            String host = "", ip = "", so = "", arq = "", car = "";
            if (e instanceof Computadora c) {
                host = c.getHostname(); ip = c.getIp(); so = c.getSistemaOperativo();
                arq = c.getArquitectura(); car = c.getCaracteristicas();
            }
            filas.add(new String[] { String.valueOf(e.getId()), e.getTipo(), e.getCodigoInventario(),
                    e.getNumSerie(), e.getMarca(), e.getModelo(), e.getFechaAdquisicion(), e.getEstado(),
                    String.valueOf(e.getLaboratorioId()), host, ip, so, arq, car });
        }
        archivo.escribir(filas);
    }
}

