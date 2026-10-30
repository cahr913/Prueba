package com.labcontrol.UI;

import com.labcontrol.modelo.Mantenimiento;
import com.labcontrol.modelo.Pieza;
import com.labcontrol.servicio.MantenimientoServicio;
import com.labcontrol.servicio.PiezaServicio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class FrmPieza extends javax.swing.JFrame {

    private final PiezaServicio servicio = new PiezaServicio();
    private final MantenimientoServicio mantenimientoService = new MantenimientoServicio();

    private final Map<Integer, Mantenimiento> mantenimientos = new HashMap<>();

    private Pieza editada;

    private final JComboBox<OpcionCombo> cboMantenimiento = new JComboBox<>();
    private final JTextField txtNombre = new JTextField(16);
    private final JTextArea txtDescripcion = new JTextArea(2, 26);
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JComboBox<String> cboAccion = new JComboBox<>(new String[]{"REPARACION", "REEMPLAZO"});
    private final JTextField txtObservaciones = new JTextField(16);

    private final JButton btnGuardar = new JButton("Guardar nueva pieza");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private DefaultTableModel modeloTabla;
    private final javax.swing.JTable tblPiezas = new javax.swing.JTable();

    public FrmPieza() {
        super("Piezas Reparadas o Reemplazadas");
        construir();
        recargarCombos();
        btnActualizar.setEnabled(false);
    }

    private void construir() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("  Piezas de Mantenimiento Correctivo");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos de la pieza"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; form.add(new JLabel("Mantenimiento (Nº de servicio):"), c);
        c.gridx = 2; c.gridwidth = 4; form.add(cboMantenimiento, c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; form.add(new JLabel("Nombre de la pieza:"), c);
        c.gridx = 1; form.add(txtNombre, c);
        c.gridx = 2; form.add(new JLabel("Cantidad:"), c);
        c.gridx = 3; form.add(spnCantidad, c);
        c.gridx = 4; form.add(new JLabel("Acción:"), c);
        c.gridx = 5; form.add(cboAccion, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Descripción:"), c);
        c.gridx = 1; c.gridwidth = 5; form.add(new JScrollPane(txtDescripcion), c);

        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Observaciones:"), c);
        c.gridx = 1; c.gridwidth = 5; form.add(txtObservaciones, c);
        add(form, BorderLayout.NORTH);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        sur.add(botones, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Nº servicio", "Equipo", "Pieza", "Cantidad", "Acción"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblPiezas.setModel(modeloTabla);
        tblPiezas.setRowHeight(24);
        tblPiezas.getTableHeader().setReorderingAllowed(false);
        tblPiezas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionada();
            }
        });
        JScrollPane scroll = new JScrollPane(tblPiezas);
        scroll.setPreferredSize(new Dimension(850, 250));
        sur.add(scroll, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        setSize(950, 560);
        setLocationRelativeTo(null);
    }

    private void recargarCombos() {
        mantenimientos.clear();
        cboMantenimiento.removeAllItems();
        try {
            for (Mantenimiento m : mantenimientoService.listar()) {
                mantenimientos.put(m.getId(), m);
                cboMantenimiento.addItem(new OpcionCombo(m.getId(),
                        m.getNumeroServicio() + " (" + m.tipoValorBd() + " - "
                                + m.getEstadoTexto() + ")"));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los mantenimientos: " + e.getMessage());
        }
        if (cboMantenimiento.getItemCount() > 0) {
            cboMantenimiento.setSelectedIndex(0);
        }
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Pieza p : servicio.listar()) {
                Mantenimiento m = mantenimientos.get(p.getMantenimientoId());
                modeloTabla.addRow(new Object[]{
                        p.getId(),
                        m != null ? m.getNumeroServicio() : "Nº " + p.getMantenimientoId(),
                        m != null ? "Equipo " + m.getEquipoId() : "-",
                        p.getNombre(),
                        p.getCantidad(),
                        p.getAccion() != null ? p.getAccion().name() : "-"
                });
            }
        } catch (SQLException e) {
            Ui.error(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void cargarSeleccionada() {
        int fila = tblPiezas.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (int) tblPiezas.getValueAt(fila, 0);
        try {
            Pieza p = servicio.buscarPorId(id).orElse(null);
            if (p == null) {
                return;
            }
            editada = p;
            seleccionarPorId(cboMantenimiento, p.getMantenimientoId());
            txtNombre.setText(p.getNombre());
            txtDescripcion.setText(p.getDescripcion());
            spnCantidad.setValue(p.getCantidad());
            cboAccion.setSelectedItem(p.getAccion() != null ? p.getAccion().name() : "REPARACION");
            txtObservaciones.setText(p.getObservaciones());
            btnActualizar.setEnabled(true);
        } catch (SQLException e) {
            Ui.error(this, "Error al cargar la pieza: " + e.getMessage());
        }
    }

    private void seleccionarPorId(JComboBox<OpcionCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private void guardar() {
        Pieza p = crearObjetoDesdeFormulario(true);
        if (p == null) {
            return;
        }
        try {
            servicio.registrar(p);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Pieza registrada correctamente.");
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (editada == null) {
            Ui.aviso(this, "Seleccione una pieza de la tabla para actualizarla.");
            return;
        }
        Pieza p = crearObjetoDesdeFormulario(false);
        if (p == null) {
            return;
        }
        try {
            servicio.actualizar(p);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Pieza actualizada correctamente.");
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        int fila = tblPiezas.getSelectedRow();
        if (fila < 0) {
            Ui.aviso(this, "Seleccione una pieza de la tabla para eliminarla.");
            return;
        }
        int id = (int) tblPiezas.getValueAt(fila, 0);
        if (!Ui.confirmar(this, "¿Desea eliminar la pieza seleccionada?")) {
            return;
        }
        try {
            servicio.eliminar(id);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Pieza eliminada correctamente.");
        } catch (SQLException e) {
            Ui.error(this, "No se pudo eliminar: " + e.getMessage());
        }
    }

    private Pieza crearObjetoDesdeFormulario(boolean nuevo) {
        if (cboMantenimiento.getItemCount() == 0 || cboMantenimiento.getSelectedItem() == null) {
            Ui.aviso(this, "No hay mantenimientos registrados. Registre un mantenimiento primero.");
            return null;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Ui.aviso(this, "Escriba el nombre de la pieza.");
            txtNombre.requestFocus();
            return null;
        }
        Pieza p = new Pieza();
        if (!nuevo && editada != null) {
            p.setId(editada.getId());
        }
        p.setMantenimientoId(((OpcionCombo) cboMantenimiento.getSelectedItem()).getId());
        p.setNombre(nombre);
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setCantidad((int) spnCantidad.getValue());
        p.setAccion(com.labcontrol.modelo.Accion.valueOf((String) cboAccion.getSelectedItem()));
        p.setObservaciones(txtObservaciones.getText().trim());
        return p;
    }

    private void limpiar() {
        editada = null;
        tblPiezas.clearSelection();
        txtNombre.setText("");
        txtDescripcion.setText("");
        spnCantidad.setValue(1);
        cboAccion.setSelectedIndex(0);
        txtObservaciones.setText("");
        if (cboMantenimiento.getItemCount() > 0) {
            cboMantenimiento.setSelectedIndex(0);
        }
        btnActualizar.setEnabled(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmPieza().setVisible(true));
    }
}
