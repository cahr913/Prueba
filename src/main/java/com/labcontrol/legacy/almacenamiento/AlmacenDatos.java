package com.labcontrol.legacy.almacenamiento;

import com.labcontrol.modelo.CentroEducativo;
import com.labcontrol.modelo.Horario;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Usuario;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlmacenDatos {

    public static final String ACTIVO = "Activo";
    public static final String INACTIVO = "Inactivo";
    public static final String EN_MANTENIMIENTO = "En mantenimiento";

    public static final String[] ESTADOS_LABORATORIO = {ACTIVO, INACTIVO, EN_MANTENIMIENTO};
    public static final String[] ESTADOS_USUARIO = {ACTIVO, INACTIVO};
    public static final String[] ROLES = {"Administrador", "Docente", "Encargado de laboratorio"};
    public static final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    private final List<CentroEducativo> centros = new ArrayList<>();
    private final List<Laboratorio> laboratorios = new ArrayList<>();
    private final List<Horario> horarios = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    private int ultimoCodigoLaboratorio;
    private int ultimoCodigoHorario;
    private int ultimoCodigoUsuario;
    private Usuario usuarioActual;

    public AlmacenDatos() {
        cargarDatosDeEjemplo();
    }

    private void cargarDatosDeEjemplo() {
        Usuario admin = new Usuario(siguienteCodigoUsuario(), "Administrador del sistema", "admin", "admin123",
                "Administrador", ACTIVO);
        Usuario docente = new Usuario(siguienteCodigoUsuario(), "María Rodríguez", "mrodriguez", "clave123",
                "Docente", ACTIVO);
        Usuario encargado = new Usuario(siguienteCodigoUsuario(), "Carlos Méndez", "cmendez", "clave123",
                "Encargado de laboratorio", INACTIVO);
        usuarios.add(admin);
        usuarios.add(docente);
        usuarios.add(encargado);

        CentroEducativo central = new CentroEducativo("CE-001", "Instituto Tecnológico Central",
                "6a Avenida 5-20, Zona 1", "2230-1122");
        CentroEducativo sanLucas = new CentroEducativo("CE-002", "Colegio San Lucas",
                "12 Calle 8-45, Zona 10", "2368-4455");
        CentroEducativo norte = new CentroEducativo("CE-003", "Centro Universitario Norte",
                "Kilómetro 15 Carretera al Atlántico", "2419-7788");
        centros.add(central);
        centros.add(sanLucas);
        centros.add(norte);

        Laboratorio computoA = new Laboratorio(siguienteCodigoLaboratorio(), "Laboratorio de Cómputo A", 30,
                "Edificio A, segundo nivel", ACTIVO, central);
        Laboratorio computoB = new Laboratorio(siguienteCodigoLaboratorio(), "Laboratorio de Cómputo B", 25,
                "Edificio A, segundo nivel", EN_MANTENIMIENTO, central);
        Laboratorio quimica = new Laboratorio(siguienteCodigoLaboratorio(), "Laboratorio de Química", 20,
                "Edificio B, primer nivel", ACTIVO, sanLucas);
        Laboratorio fisica = new Laboratorio(siguienteCodigoLaboratorio(), "Laboratorio de Física", 24,
                "Edificio B, segundo nivel", ACTIVO, sanLucas);
        Laboratorio redes = new Laboratorio(siguienteCodigoLaboratorio(), "Laboratorio de Redes", 18,
                "Torre 2, tercer nivel", INACTIVO, norte);
        laboratorios.add(computoA);
        laboratorios.add(computoB);
        laboratorios.add(quimica);
        laboratorios.add(fisica);
        laboratorios.add(redes);

        horarios.add(new Horario(siguienteCodigoHorario(), computoA, "Lunes", LocalTime.of(8, 0),
                LocalTime.of(10, 0), "Programación I", docente));
        horarios.add(new Horario(siguienteCodigoHorario(), computoA, "Lunes", LocalTime.of(10, 0),
                LocalTime.of(12, 0), "Bases de datos", admin));
        horarios.add(new Horario(siguienteCodigoHorario(), computoA, "Miércoles", LocalTime.of(14, 0),
                LocalTime.of(16, 0), "Taller de ofimática", docente));
        horarios.add(new Horario(siguienteCodigoHorario(), quimica, "Martes", LocalTime.of(8, 0),
                LocalTime.of(9, 30), "Práctica de reactivos", admin));
        horarios.add(new Horario(siguienteCodigoHorario(), fisica, "Jueves", LocalTime.of(10, 0),
                LocalTime.of(12, 0), "Mecánica experimental", docente));
        horarios.add(new Horario(siguienteCodigoHorario(), quimica, "Viernes", LocalTime.of(13, 0),
                LocalTime.of(15, 0), "Titulaciones", admin));
    }

    public List<CentroEducativo> getCentros() {
        return Collections.unmodifiableList(centros);
    }

    public List<Laboratorio> getLaboratorios() {
        return Collections.unmodifiableList(laboratorios);
    }

    public List<Horario> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public String siguienteCodigoLaboratorio() {
        ultimoCodigoLaboratorio++;
        return String.format("LAB-%03d", ultimoCodigoLaboratorio);
    }

    public String siguienteCodigoHorario() {
        ultimoCodigoHorario++;
        return String.format("HOR-%03d", ultimoCodigoHorario);
    }

    public String siguienteCodigoUsuario() {
        ultimoCodigoUsuario++;
        return String.format("USR-%03d", ultimoCodigoUsuario);
    }

    public void agregarCentro(CentroEducativo centro) {
        centros.add(centro);
    }

    public void eliminarCentro(CentroEducativo centro) {
        centros.remove(centro);
    }

    public void agregarLaboratorio(Laboratorio laboratorio) {
        laboratorios.add(laboratorio);
    }

    public void eliminarLaboratorio(Laboratorio laboratorio) {
        laboratorios.remove(laboratorio);
    }

    public void agregarHorario(Horario horario) {
        horarios.add(horario);
    }

    public void eliminarHorario(Horario horario) {
        horarios.remove(horario);
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void eliminarUsuario(Usuario usuario) {
        usuarios.remove(usuario);
    }

    public boolean existeCodigoCentro(String codigo, CentroEducativo excepto) {
        for (CentroEducativo centro : centros) {
            if (centro != excepto && centro.getCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeLaboratorio(String nombre, CentroEducativo centro, Laboratorio excepto) {
        for (Laboratorio laboratorio : laboratorios) {
            if (laboratorio != excepto && laboratorio.getCentro() == centro
                    && laboratorio.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeNombreUsuario(String nombreUsuario, Usuario excepto) {
        for (Usuario usuario : usuarios) {
            if (usuario != excepto && usuario.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }

    public int contarLaboratoriosDeCentro(CentroEducativo centro) {
        int total = 0;
        for (Laboratorio laboratorio : laboratorios) {
            if (laboratorio.getCentro() == centro) {
                total++;
            }
        }
        return total;
    }

    public int contarHorariosDeLaboratorio(Laboratorio laboratorio) {
        int total = 0;
        for (Horario horario : horarios) {
            if (horario.getLaboratorio() == laboratorio) {
                total++;
            }
        }
        return total;
    }

    public int contarHorariosDeUsuario(Usuario usuario) {
        int total = 0;
        for (Horario horario : horarios) {
            if (horario.getUsuario() == usuario) {
                total++;
            }
        }
        return total;
    }

    public Horario buscarHorarioSuperpuesto(Laboratorio laboratorio, String dia, LocalTime inicio, LocalTime fin,
            Horario excepto) {
        for (Horario horario : horarios) {
            if (horario != excepto && horario.getLaboratorio() == laboratorio && horario.getDia().equals(dia)
                    && inicio.isBefore(horario.getHoraFin()) && horario.getHoraInicio().isBefore(fin)) {
                return horario;
            }
        }
        return null;
    }
}


