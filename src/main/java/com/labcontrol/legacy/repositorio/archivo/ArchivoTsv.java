package com.labcontrol.legacy.repositorio.archivo;

import com.labcontrol.legacy.repositorio.RepositorioException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Lee y escribe un archivo de texto con columnas separadas por tabulador (UTF-8). */
public final class ArchivoTsv {
    private final Path ruta;

    public ArchivoTsv(String nombre) {
        try {
            Files.createDirectories(carpetaDatos());
        } catch (IOException e) {
            throw new RepositorioException("No se pudo crear la carpeta de datos: " + carpetaDatos(), e);
        }
        this.ruta = carpetaDatos().resolve(nombre);
    }

    /** Por defecto: carpeta del usuario/LabControl/datos. Se puede cambiar con -Dlabcontrol.datos=RUTA */
    public static Path carpetaDatos() {
        String p = System.getProperty("labcontrol.datos");
        if (p != null && !p.isBlank()) return Paths.get(p);
        return Paths.get(System.getProperty("user.home"), "LabControl", "datos");
    }

    public List<String[]> leer() {
        List<String[]> filas = new ArrayList<>();
        if (!Files.exists(ruta)) return filas;
        try {
            for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split("\t", -1);
                for (int i = 0; i < partes.length; i++) partes[i] = desescapar(partes[i]);
                filas.add(partes);
            }
        } catch (IOException e) {
            throw new RepositorioException("No se pudo leer el archivo " + ruta, e);
        }
        return filas;
    }

    public void escribir(List<String[]> filas) {
        List<String> lineas = new ArrayList<>();
        for (String[] fila : filas) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fila.length; i++) {
                if (i > 0) sb.append('\t');
                sb.append(escapar(fila[i]));
            }
            lineas.add(sb.toString());
        }
        Path tmp = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        try {
            Files.write(tmp, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(tmp, ruta, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, ruta, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RepositorioException("No se pudo guardar en el archivo " + ruta, e);
        }
    }

    public static int entero(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static String escapar(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\t' -> sb.append("\\t");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String desescapar(String s) {
        if (s.indexOf('\\') < 0) return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                switch (n) {
                    case 't' -> sb.append('\t');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    default -> sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

