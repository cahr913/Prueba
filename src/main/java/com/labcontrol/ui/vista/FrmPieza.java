/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.labcontrol.ui.vista;
import com.labcontrol.modelo.Pieza;
import com.labcontrol.servicio.PiezaService;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
/**
 *
 * @author hp
 */
public class FrmPieza extends javax.swing.JFrame {
    private final PiezaService servicio = new PiezaService();
    private DefaultTableModel modeloTabla;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8;
    private javax.swing.JTextField txtIDPieza, txtNombre, txtCantidad, txtTipoOperacion;
    private javax.swing.JButton btnGuardar, btnBuscar, btnEliminar, btnLimpiar;
    private javax.swing.JTable tblPiezas;
    private javax.swing.JScrollPane jScrollPane1;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmPieza.class.getName());

    public FrmPieza() {
        initComponents();
        configurarTabla();
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre de pieza", "Cantidad", "Tipo de operación"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tblPiezas.setModel(modeloTabla);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Pieza pieza : servicio.listar()) {
                modeloTabla.addRow(new Object[]{pieza.getIdPieza(), pieza.getNombre(), pieza.getCantidad(), pieza.getTipoOperacion()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtIDPieza.setText(""); txtNombre.setText(""); txtCantidad.setText(""); txtTipoOperacion.setText("");
        tblPiezas.clearSelection();
    }

    private void initComponents() {
        jPanel1 = new javax.swing.JPanel(new GridLayout(4, 2, 10, 10));
        jLabel1 = new javax.swing.JLabel("PIEZAS");
        jLabel2 = new javax.swing.JLabel("Sistema de Gestion de Laboratorios");
        jLabel3 = new javax.swing.JLabel("ID de pieza:");
        jLabel4 = new javax.swing.JLabel("Nombre de pieza:");
        jLabel5 = new javax.swing.JLabel("Cantidad:");
        jLabel6 = new javax.swing.JLabel("Tipo de operacion:");
        jLabel7 = new javax.swing.JLabel("");
        jLabel8 = new javax.swing.JLabel("Tabla de piezas");

        txtIDPieza = new javax.swing.JTextField(15);
        txtNombre = new javax.swing.JTextField(15);
        txtCantidad = new javax.swing.JTextField(15);
        txtTipoOperacion = new javax.swing.JTextField(15);

        btnGuardar = new javax.swing.JButton("Guardar");
        btnBuscar = new javax.swing.JButton("Buscar");
        btnEliminar = new javax.swing.JButton("Eliminar");
        btnLimpiar = new javax.swing.JButton("Limpiar");

        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        tblPiezas = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane(tblPiezas);

        jPanel1.add(jLabel3); jPanel1.add(txtIDPieza);
        jPanel1.add(jLabel4); jPanel1.add(txtNombre);
        jPanel1.add(jLabel5); jPanel1.add(txtCantidad);
        jPanel1.add(jLabel6); jPanel1.add(txtTipoOperacion);
        jPanel1.add(btnGuardar); jPanel1.add(btnBuscar);
        jPanel1.add(btnEliminar); jPanel1.add(btnLimpiar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pack();
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int id = Integer.parseInt(txtIDPieza.getText().trim());
            String nombre = txtNombre.getText().trim();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            Pieza.TipoOperacion tipo = Pieza.TipoOperacion.valueOf(txtTipoOperacion.getText().trim().toUpperCase());
            Pieza pieza = new Pieza(id, nombre, cantidad, tipo);
            servicio.registrar(pieza);
            actualizarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Pieza registrada correctamente.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID y la cantidad deben ser números enteros.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int id = Integer.parseInt(txtIDPieza.getText().trim());
            Pieza pieza = servicio.buscarPorId(id).orElse(null);
            if (pieza == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la pieza.");
                return;
            }
            txtNombre.setText(pieza.getNombre());
            txtCantidad.setText(String.valueOf(pieza.getCantidad()));
            txtTipoOperacion.setText(pieza.getTipoOperacion().name());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de pieza válido.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int id = Integer.parseInt(txtIDPieza.getText().trim());
            Pieza pieza = servicio.buscarPorId(id).orElse(null);
            if (pieza == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la pieza.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea eliminar la pieza " + pieza.getNombre() + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                servicio.eliminar(id);
                actualizarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Pieza eliminada correctamente.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de pieza válido.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FrmPieza().setVisible(true));
    }
}
