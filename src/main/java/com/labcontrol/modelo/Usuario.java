package com.labcontrol.modelo;

import java.time.LocalDate;

public class Usuario {
    private int idUsuario;
    private String codigo;
    private String nombre;
    private String apellido;
    private String usuario;
    private String contrasena;
    private String correo;
    private String rol;
    private String estado;
    private LocalDate fechaCreacion;

    public Usuario() {
    }

    public Usuario(String codigo, String nombre, String usuario, String clave, String rol, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = clave;
        this.rol = rol;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getClave() { return contrasena; }
    public void setClave(String clave) { this.contrasena = clave; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public void setEstado(boolean activo) {
        this.estado = activo ? "Activo" : "Inactivo";
    }

    public boolean isEstado() {
        return "Activo".equalsIgnoreCase(estado);
    }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean autenticar() { return true; }
    public void cambiarContrasena() {}
    public void cerrarSesion() {}
    public void actualizarDatos() {}
    public String obtenerRol() { return rol != null ? rol : "Usuario"; }

    @Override
    public String toString() {
        return (usuario != null ? usuario : "Sin usuario") + " (" + (rol != null ? rol : "sin rol") + ")";
    }
}
