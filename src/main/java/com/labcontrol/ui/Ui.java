package com.labcontrol.UI;

import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.modelo.Laboratorio;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public final class Ui {

    public static final String TODOS = "Todos";

    private Ui() {
    }

    public static void instalarAspecto() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el aspecto Nimbus: " + e.getMessage());
        }
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
    }

    public static void estiloTitulo(JLabel titulo) {
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
    }

    public static void estiloEncabezado(JLabel titulo) {
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
    }

    public static void escCierra(JDialog dialogo) {
        dialogo.getRootPane().registerKeyboardAction(
                e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static String texto(JComboBox<?> combo) {
        Object valor = combo.getSelectedItem();
        return valor == null ? "" : valor.toString();
    }

    public static boolean fechaValida(String fecha) {
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void cargarLaboratorios(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            LaboratorioDao dao = new LaboratorioDao();
            for (Laboratorio laboratorio : dao.listar()) {
                combo.addItem(laboratorio.getCodigo());
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron cargar los laboratorios: " + e.getMessage());
        }
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void fallo(Component padre, String mensaje) {
        error(padre, mensaje);
    }

    public static void configurarTabla(JTable tabla, int... anchos) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    public static int filaSeleccionada(Component padre, JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            aviso(padre, "Seleccione un registro de la tabla para continuar.");
            return -1;
        }
        return tabla.convertRowIndexToModel(fila);
    }

    public static void habilitar(Container contenedor, boolean habilitado) {
        for (Component componente : contenedor.getComponents()) {
            if (!(componente instanceof JLabel)) {
                componente.setEnabled(habilitado);
            }
        }
    }

    public static void recargarCombo(JComboBox<Object> combo, List<?> elementos, boolean conTodos) {
        Object seleccionado = combo.getSelectedItem();
        combo.removeAllItems();
        if (conTodos) {
            combo.addItem(TODOS);
        }
        for (Object elemento : elementos) {
            combo.addItem(elemento);
        }
        if (seleccionado != null) {
            combo.setSelectedItem(seleccionado);
        }
        if (combo.getSelectedIndex() < 0 && combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void info(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }
}

