package com.labcontrol.legacy.repositorio;

/** Error al leer o guardar datos (archivo hoy, base de datos mañana). */
public class RepositorioException extends RuntimeException {
    public RepositorioException(String mensaje) { super(mensaje); }
    public RepositorioException(String mensaje, Throwable causa) { super(mensaje, causa); }
}

