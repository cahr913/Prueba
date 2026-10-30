package com.labcontrol.UI;

import com.labcontrol.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FrmPrincipal extends javax.swing.JFrame {

    private static final String LOGIN = "login";
    private static final String CENTROS = "centros";
    private static final String LABORATORIOS = "laboratorios";
    private static final String HORARIOS = "horarios";
    private static final String USUARIOS = "usuarios";

    private PnlLogin pnlLogin;
    private PnlCentros pnlCentros;
    private PnlLaboratorios pnlLaboratorios;
    private PnlHorarios pnlHorarios;
    private PnlUsuarios pnlUsuarios;

    private FrmEquipo frmEquipo;
    private FrmComputadora frmComputadora;
    private FrmSoftware frmSoftware;
    private FrmAsistenteInstalacion frmInstalacion;
    private FrmRecepcion frmRecepcion;
    private FrmReporteDano frmReporteDano;
    private FrmMantenimiento frmMantenimiento;
    private FrmPieza frmPieza;
    private FrmReportes frmReportes;

    private JPanel pnlContenido;
    private JPanel pnlMenu;
    private Usuario usuarioActual;

    private JButton btnCentros, btnLaboratorios, btnHorarios, btnUsuarios;
    private JButton btnEquipos, btnComputadoras, btnSoftware, btnInstalacion;
    private JButton btnRecepcion, btnReporteDano, btnMantenimiento, btnPiezas;
    private JButton btnReportes, btnAyuda, btnCerrarSesion;

    public FrmPrincipal() {
        construir();
        iniciar();
    }

    private void construir() {
        setTitle("Sistema de Gestion de Laboratorios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        setSize(1300, 800);
        setLocationRelativeTo(null);

        JLabel titulo = new JLabel("  Sistema de Gestion de Laboratorios de Computo", SwingConstants.LEFT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(31, 78, 121));
        titulo.setForeground(Color.WHITE);
        titulo.setPreferredSize(new Dimension(0, 42));
        add(titulo, BorderLayout.NORTH);

        pnlContenido = new JPanel(new CardLayout());
        add(pnlContenido, BorderLayout.CENTER);

        pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        pnlMenu.setPreferredSize(new Dimension(210, 0));
        pnlMenu.setBackground(new Color(245, 247, 250));
        add(pnlMenu, BorderLayout.WEST);
    }

    private void iniciar() {
        pnlLogin = new PnlLogin(this::iniciarSesion);
        pnlCentros = new PnlCentros();
        pnlLaboratorios = new PnlLaboratorios();
        pnlHorarios = new PnlHorarios();
        pnlUsuarios = new PnlUsuarios();
        frmEquipo = new FrmEquipo(this, true);
        frmComputadora = new FrmComputadora(this, true);
        frmSoftware = new FrmSoftware(this, true);
        frmInstalacion = new FrmAsistenteInstalacion(this, true);
        frmRecepcion = new FrmRecepcion();
        frmReporteDano = new FrmReporteDano();
        frmMantenimiento = new FrmMantenimiento();
        frmPieza = new FrmPieza();
        frmReportes = new FrmReportes();

        pnlContenido.add(pnlLogin, LOGIN);
        pnlContenido.add(pnlCentros, CENTROS);
        pnlContenido.add(pnlLaboratorios, LABORATORIOS);
        pnlContenido.add(pnlHorarios, HORARIOS);
        pnlContenido.add(pnlUsuarios, USUARIOS);

        construirMenu();

        pnlMenu.setVisible(false);
        ((CardLayout) pnlContenido.getLayout()).show(pnlContenido, LOGIN);
    }

    private void construirMenu() {
        btnCentros = boton("Centros Educativos");
        btnLaboratorios = boton("Laboratorios");
        btnHorarios = boton("Horarios");
        btnUsuarios = boton("Usuarios");
        btnEquipos = boton("Equipos");
        btnComputadoras = boton("Computadoras");
        btnSoftware = boton("Software");
        btnInstalacion = boton("Instalacion de Software");
        btnRecepcion = boton("Recepcion de Lab");
        btnReporteDano = boton("Reportes de Daño");
        btnMantenimiento = boton("Mantenimiento");
        btnPiezas = boton("Piezas");
        btnReportes = boton("Consultas y Reportes");
        btnAyuda = boton("Ayuda");
        btnCerrarSesion = boton("Cerrar sesion");

        btnCentros.addActionListener(e -> mostrarSeccion(CENTROS, pnlCentros, btnCentros));
        btnLaboratorios.addActionListener(e -> mostrarSeccion(LABORATORIOS, pnlLaboratorios, btnLaboratorios));
        btnHorarios.addActionListener(e -> mostrarSeccion(HORARIOS, pnlHorarios, btnHorarios));
        btnUsuarios.addActionListener(e -> mostrarSeccion(USUARIOS, pnlUsuarios, btnUsuarios));
        btnEquipos.addActionListener(e -> mostrarVentana(frmEquipo));
        btnComputadoras.addActionListener(e -> mostrarVentana(frmComputadora));
        btnSoftware.addActionListener(e -> mostrarVentana(frmSoftware));
        btnInstalacion.addActionListener(e -> mostrarVentana(frmInstalacion));
        btnRecepcion.addActionListener(e -> mostrarVentana(frmRecepcion));
        btnReporteDano.addActionListener(e -> mostrarVentana(frmReporteDano));
        btnMantenimiento.addActionListener(e -> mostrarVentana(frmMantenimiento));
        btnPiezas.addActionListener(e -> mostrarVentana(frmPieza));
        btnReportes.addActionListener(e -> mostrarVentana(frmReportes));
        btnAyuda.addActionListener(e -> mostrarAyuda());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        pnlMenu.add(new JLabel("  Menu principal", SwingConstants.LEFT));
        pnlMenu.add(Box.createVerticalStrut(6));
        agregarBoton(btnCentros);
        agregarBoton(btnLaboratorios);
        agregarBoton(btnHorarios);
        agregarBoton(btnUsuarios);
        agregarBoton(btnEquipos);
        agregarBoton(btnComputadoras);
        agregarBoton(btnSoftware);
        agregarBoton(btnInstalacion);
        agregarBoton(btnRecepcion);
        agregarBoton(btnReporteDano);
        agregarBoton(btnMantenimiento);
        agregarBoton(btnPiezas);
        agregarBoton(btnReportes);
        pnlMenu.add(Box.createVerticalGlue());
        agregarBoton(btnAyuda);
        agregarBoton(btnCerrarSesion);
    }

    private JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        return b;
    }

    private void agregarBoton(JButton b) {
        pnlMenu.add(b);
        pnlMenu.add(Box.createVerticalStrut(3));
    }

    private void mostrarSeccion(String tarjeta, JPanel panel, JButton activo) {
        if (panel instanceof Refrescable) {
            ((Refrescable) panel).refrescar();
        }
        ((CardLayout) pnlContenido.getLayout()).show(pnlContenido, tarjeta);
    }

    private void mostrarVentana(java.awt.Window ventana) {
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    private void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        pnlLogin.limpiar();
        pnlMenu.setVisible(true);

        String rol = usuario.getRol();
        btnCentros.setVisible("Administrador".equalsIgnoreCase(rol));
        btnLaboratorios.setVisible(true);
        btnHorarios.setVisible(!"Responsable de laboratorio".equalsIgnoreCase(rol));
        btnUsuarios.setVisible("Administrador".equalsIgnoreCase(rol));
        btnEquipos.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnComputadoras.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnSoftware.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnInstalacion.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnRecepcion.setVisible(!"Responsable de laboratorio".equalsIgnoreCase(rol));
        btnReporteDano.setVisible(!"Responsable de laboratorio".equalsIgnoreCase(rol));
        btnMantenimiento.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnPiezas.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));
        btnReportes.setVisible("Administrador".equalsIgnoreCase(rol) || "Responsable de laboratorio".equalsIgnoreCase(rol));

        if ("Administrador".equalsIgnoreCase(rol)) {
            mostrarSeccion(CENTROS, pnlCentros, btnCentros);
        } else {
            mostrarSeccion(LABORATORIOS, pnlLaboratorios, btnLaboratorios);
        }
    }

    private void cerrarSesion() {
        this.usuarioActual = null;
        pnlMenu.setVisible(false);
        pnlLogin.limpiar();
        ((CardLayout) pnlContenido.getLayout()).show(pnlContenido, LOGIN);
    }

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestion de Laboratorios\n" +
                        "Version 1.0\n" +
                        "Programacion II - Universidad Mariano Galvez de Guatemala\n\n" +
                        "Funcionalidades:\n" +
                        "- Gestion de Centros Educativos (con logotipo)\n" +
                        "- Gestion de Laboratorios\n" +
                        "- Programacion de Horarios (sin solapamiento)\n" +
                        "- Gestion de Usuarios por rol\n" +
                        "- Gestion de Equipos y Computadoras\n" +
                        "- Gestion de Software\n" +
                        "- Instalacion de Software\n" +
                        "- Recepcion de Laboratorios\n" +
                        "- Reporte de Daños (con fotos)\n" +
                        "- Control de Mantenimiento (Preventivo / Correctivo)\n" +
                        "- Gestion de Piezas\n" +
                        "- Consultas y Reportes (11 reportes)\n" +
                        "- Hoja de Servicio en PDF",
                "Ayuda - Sistema de Laboratorios",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
