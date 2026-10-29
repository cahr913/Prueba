package com.labcontrol.ui.vista;

import com.labcontrol.legacy.repositorio.Repositorios;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Controla qué ventana principal está visible (Equipos / Catálogo de software)
 * y agrupa las acciones comunes que invocan los menús de los formularios.
 */
public final class Navegacion {
    private static FrmPrincipal principal;
    private static FrmCatalogoSoftware catalogo;

    private Navegacion() { }

    public static void iniciar() {
        principal = new FrmPrincipal();
        principal.setVisible(true);
    }

    public static void irAEquipos(JFrame actual) {
        if (principal == null) principal = new FrmPrincipal();
        cambiar(actual, principal);
    }

    public static void irACatalogo(JFrame actual) {
        if (catalogo == null) catalogo = new FrmCatalogoSoftware();
        cambiar(actual, catalogo);
    }

    private static void cambiar(JFrame de, JFrame a) {
        if (de == a) return;
        a.setBounds(de.getBounds());
        de.setVisible(false);
        a.setVisible(true);
    }

    /** Pregunta qué tipo de equipo se va a registrar y abre el formulario que corresponde. */
    public static void nuevoEquipo(Frame padre) {
        String[] opciones = { "Computadora", "Impresora", "Otro equipo", "Cancelar" };
        int r = JOptionPane.showOptionDialog(padre, "¿Qué tipo de equipo desea registrar?", "Nuevo equipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (r == 0) FrmComputadora.abrir(padre, null);
        else if (r == 1) FrmEquipo.abrir(padre, null, "Impresora");
        else if (r == 2) FrmEquipo.abrir(padre, null, "Otro");
    }

    public static void acercaDe(Component padre) {
        Ui.info(padre, "LabControl 1.0\nSistema de Laboratorios - Módulo de equipo y software\n\n"
                + "Los datos se guardan en:\n" + Repositorios.carpetaDatos());
    }
}

