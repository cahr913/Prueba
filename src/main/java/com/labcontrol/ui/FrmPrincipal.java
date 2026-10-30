package com.labcontrol.ui.vista;

import com.labcontrol.modelo.Usuario;
import com.labcontrol.ui.vista.FrmEquipo;
import com.labcontrol.ui.vista.FrmComputadora;
import com.labcontrol.ui.vista.FrmSoftware;
import com.labcontrol.ui.vista.FrmAsistenteInstalacion;
import com.labcontrol.ui.vista.FrmRecepcion;
import com.labcontrol.ui.vista.FrmReporteDano;
import com.labcontrol.ui.vista.FrmMantenimiento;
import com.labcontrol.ui.vista.FrmPieza;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class FrmPrincipal extends javax.swing.JFrame {

    private static final String LOGIN = "login";
    private static final String CENTROS = "centros";
    private static final String LABORATORIOS = "laboratorios";
    private static final String HORARIOS = "horarios";
    private static final String USUARIOS = "usuarios";
    private static final String EQUIPOS = "equipos";
    private static final String COMPUTADORAS = "computadoras";
    private static final String SOFTWARE = "software";
    private static final String INSTALACION = "instalacion";
    private static final String RECEPCION = "recepcion";
    private static final String REPORTE_DANO = "reporte_dano";
    private static final String MANTENIMIENTO = "mantenimiento";
    private static final String PIEZAS = "piezas";

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

    private JButton[] botonesSeccion;
    private Usuario usuarioActual;

    public FrmPrincipal() {
        initComponents();
        iniciar();
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

        pnlContenido.add(pnlLogin, LOGIN);
        pnlContenido.add(pnlCentros, CENTROS);
        pnlContenido.add(pnlLaboratorios, LABORATORIOS);
        pnlContenido.add(pnlHorarios, HORARIOS);
        pnlContenido.add(pnlUsuarios, USUARIOS);

        botonesSeccion = new JButton[]{btnCentros, btnLaboratorios, btnHorarios, btnUsuarios, btnEquipos, btnComputadoras, btnSoftware, btnInstalacion, btnRecepcion, btnReporteDano, btnMantenimiento, btnPiezas};

        pnlMenu.setVisible(false);
        setMinimumSize(new Dimension(1200, 720));
        setSize(1300, 800);
        setLocationRelativeTo(null);
        ((CardLayout) pnlContenido.getLayout()).show(pnlContenido, LOGIN);
    }

    private void mostrarSeccion(String tarjeta, javax.swing.JPanel panel, JButton activo) {
        if (panel instanceof Refrescable) {
            ((Refrescable) panel).refrescar();
        }
        ((CardLayout) pnlContenido.getLayout()).show(pnlContenido, tarjeta);
        for (JButton boton : botonesSeccion) {
            if (boton != null && boton.isVisible()) {
                boton.setFont(boton.getFont().deriveFont(boton == activo ? Font.BOLD : Font.PLAIN));
            }
        }
    }

    private void mostrarVentanaExterna(java.awt.Window ventana) {
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    private void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        pnlLogin.limpiar();
        pnlMenu.setVisible(true);

        String rol = usuario.getRol();
        if ("Administrador".equalsIgnoreCase(rol)) {
            btnCentros.setVisible(true);
            btnLaboratorios.setVisible(true);
            btnHorarios.setVisible(true);
            btnUsuarios.setVisible(true);
            btnEquipos.setVisible(true);
            btnComputadoras.setVisible(true);
            btnSoftware.setVisible(true);
            btnInstalacion.setVisible(true);
            btnRecepcion.setVisible(true);
            btnReporteDano.setVisible(true);
            btnMantenimiento.setVisible(true);
            btnPiezas.setVisible(true);
            mostrarSeccion(CENTROS, pnlCentros, btnCentros);
        } else if ("Docente".equalsIgnoreCase(rol)) {
            btnCentros.setVisible(false);
            btnLaboratorios.setVisible(true);
            btnHorarios.setVisible(true);
            btnUsuarios.setVisible(false);
            btnEquipos.setVisible(false);
            btnComputadoras.setVisible(false);
            btnSoftware.setVisible(false);
            btnInstalacion.setVisible(false);
            btnRecepcion.setVisible(true);
            btnReporteDano.setVisible(true);
            btnMantenimiento.setVisible(false);
            btnPiezas.setVisible(false);
            mostrarSeccion(LABORATORIOS, pnlLaboratorios, btnLaboratorios);
        } else if ("Responsable de laboratorio".equalsIgnoreCase(rol)) {
            btnCentros.setVisible(false);
            btnLaboratorios.setVisible(true);
            btnHorarios.setVisible(false);
            btnUsuarios.setVisible(false);
            btnEquipos.setVisible(true);
            btnComputadoras.setVisible(true);
            btnSoftware.setVisible(true);
            btnInstalacion.setVisible(true);
            btnRecepcion.setVisible(false);
            btnReporteDano.setVisible(false);
            btnMantenimiento.setVisible(true);
            btnPiezas.setVisible(true);
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
        javax.swing.JOptionPane.showMessageDialog(this,
            "Sistema de Gestion de Laboratorios\n" +
            "Versión 1.0\n" +
            "Programación II - Universidad Mariano Gálvez de Guatemala\n\n" +
            "Funcionalidades:\n" +
            "- Gestión de Centros Educativos\n" +
            "- Gestión de Laboratorios\n" +
            "- Programación de Horarios\n" +
            "- Gestión de Usuarios\n" +
            "- Gestión de Equipos y Computadoras\n" +
            "- Gestión de Software\n" +
            "- Instalación de Software en Equipos\n" +
            "- Recepción de Laboratorios\n" +
            "- Reporte de Daños\n" +
            "- Control de Mantenimiento (Preventivo/Correctivo)\n" +
            "- Gestión de Piezas de Repuesto",
            "Ayuda - Sistema de Laboratorios",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnCentrosActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarSeccion(CENTROS, pnlCentros, btnCentros);
    }

    private void btnLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarSeccion(LABORATORIOS, pnlLaboratorios, btnLaboratorios);
    }

    private void btnHorariosActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarSeccion(HORARIOS, pnlHorarios, btnHorarios);
    }

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarSeccion(USUARIOS, pnlUsuarios, btnUsuarios);
    }

    private void btnEquiposActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmEquipo);
    }

    private void btnComputadorasActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmComputadora);
    }

    private void btnSoftwareActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmSoftware);
    }

    private void btnInstalacionActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmInstalacion);
    }

    private void btnRecepcionActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmRecepcion);
    }

    private void btnReporteDanoActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmReporteDano);
    }

    private void btnMantenimientoActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmMantenimiento);
    }

    private void btnPiezasActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarVentanaExterna(frmPieza);
    }

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarAyuda();
    }

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        cerrarSesion();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlMenu = new javax.swing.JPanel();
        btnCentros = new javax.swing.JButton();
        btnLaboratorios = new javax.swing.JButton();
        btnHorarios = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        btnEquipos = new javax.swing.JButton();
        btnComputadoras = new javax.swing.JButton();
        btnSoftware = new javax.swing.JButton();
        btnInstalacion = new javax.swing.JButton();
        btnRecepcion = new javax.swing.JButton();
        btnReporteDano = new javax.swing.JButton();
        btnMantenimiento = new javax.swing.JButton();
        btnPiezas = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        pnlContenido = new javax.swing.JPanel();
        pnlContenido.setLayout(new java.awt.CardLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Laboratorios - Infraestructura y Horarios");

        btnCentros.setText("Centros Educativos");
        btnCentros.addActionListener(this::btnCentrosActionPerformed);

        btnLaboratorios.setText("Laboratorios");
        btnLaboratorios.addActionListener(this::btnLaboratoriosActionPerformed);

        btnHorarios.setText("Horarios");
        btnHorarios.addActionListener(this::btnHorariosActionPerformed);

        btnUsuarios.setText("Usuarios");
        btnUsuarios.addActionListener(this::btnUsuariosActionPerformed);

        btnEquipos.setText("Equipos");
        btnEquipos.addActionListener(this::btnEquiposActionPerformed);

        btnComputadoras.setText("Computadoras");
        btnComputadoras.addActionListener(this::btnComputadorasActionPerformed);

        btnSoftware.setText("Software");
        btnSoftware.addActionListener(this::btnSoftwareActionPerformed);

        btnInstalacion.setText("Instalación Software");
        btnInstalacion.addActionListener(this::btnInstalacionActionPerformed);

        btnRecepcion.setText("Recepción");
        btnRecepcion.addActionListener(this::btnRecepcionActionPerformed);

        btnReporteDano.setText("Reportes de Daño");
        btnReporteDano.addActionListener(this::btnReporteDanoActionPerformed);

        btnMantenimiento.setText("Mantenimiento");
        btnMantenimiento.addActionListener(this::btnMantenimientoActionPerformed);

        btnPiezas.setText("Piezas");
        btnPiezas.addActionListener(this::btnPiezasActionPerformed);

        btnAyuda.setText("Ayuda");
        btnAyuda.addActionListener(this::btnAyudaActionPerformed);

        btnCerrarSesion.setText("Cerrar sesión");
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCentros, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLaboratorios, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHorarios, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEquipos, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnComputadoras, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSoftware, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInstalacion, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRecepcion, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReporteDano, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMantenimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPiezas, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAyuda, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCentros, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnLaboratorios, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnHorarios, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnEquipos, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnComputadoras, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnSoftware, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnInstalacion, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnRecepcion, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnReporteDano, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnMantenimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnPiezas, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnAyuda, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE))
        );

        pack();
    }

    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JButton btnCentros;
    private javax.swing.JButton btnLaboratorios;
    private javax.swing.JButton btnHorarios;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btnEquipos;
    private javax.swing.JButton btnComputadoras;
    private javax.swing.JButton btnSoftware;
    private javax.swing.JButton btnInstalacion;
    private javax.swing.JButton btnRecepcion;
    private javax.swing.JButton btnReporteDano;
    private javax.swing.JButton btnMantenimiento;
    private javax.swing.JButton btnPiezas;
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnCerrarSesion;
}