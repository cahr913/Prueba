/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.labcontrol.ui.vista;
import com.labcontrol.modelo.FotoEvidencia;
import com.labcontrol.modelo.ReporteDano;
import com.labcontrol.servicio.ReporteDanoService;

import java.awt.BorderLayout;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author hp
 */
public class FrmReporteDano extends javax.swing.JFrame {
    private final ReporteDanoService servicio = new ReporteDanoService();
    private DefaultTableModel modeloTabla;

    // Fotos de evidencia cargadas temporalmente para este reporte.
    private final DefaultListModel<String> modeloFotos = new DefaultListModel<>();
    private final List<File> archivosFoto = new ArrayList<>();
    private JList<String> lstFotos;
private void configurarTabla() {
    modeloTabla = new DefaultTableModel(
        new Object[]{
            "ID Reporte",
            "ID Recepción",
            "ID Equipo",
            "ID Docente",
            "Tipo de falla",
            "Descripción del daño",
            "Fecha del reporte",
            "Estado"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    tblReportesDano.setModel(modeloTabla);
}
private void limpiarCampos() {
    txtIDReporte.setText("");
    txtIDRecepcion.setText("");
    txtIDEquipo.setText("");
    txtIDDocente.setText("");
    txtFalla.setText("");
    txtDescripcion.setText("");
    txtEstado.setText("");
    modeloFotos.clear();
    archivosFoto.clear();
    if (lstFotos != null) {
        lstFotos.clearSelection();
    }
    txtIDReporte.requestFocus();
}
private void actualizarTabla() {
    modeloTabla.setRowCount(0);
    try {
        for (ReporteDano r : servicio.listar()) {
            modeloTabla.addRow(new Object[]{
                r.getIdReporte(),
                r.getIdRecepcion(),
                r.getIdEquipo(),
                r.getIdDocente(),
                r.getFalla(),
                r.getDescripcion(),
                r.getFechaReporte(),
                r.getEstado()
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
    }
}
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmReporteDano.class.getName());

    /**
     * Creates new form FrmReporteDano
     */
    public FrmReporteDano() {
        initComponents();
        configurarTabla();
        inicializarPanelFotos();
    }

    /** Agrega un panel inferior con la lista de fotos de evidencia y el botón para adjuntar. */
    private void inicializarPanelFotos() {
        lstFotos = new JList<>(modeloFotos);
        JScrollPane scrollFotos = new JScrollPane(lstFotos);
        scrollFotos.setPreferredSize(new java.awt.Dimension(0, 100));

        javax.swing.JButton btnAdjuntar = new javax.swing.JButton("Adjuntar foto");
        btnAdjuntar.addActionListener(e -> adjuntarFoto());

        javax.swing.JPanel panelFotos = new javax.swing.JPanel(new java.awt.BorderLayout());
        panelFotos.setBorder(javax.swing.BorderFactory.createTitledBorder("Fotos de evidencia (evidencia fotográfica)"));
        panelFotos.add(scrollFotos, java.awt.BorderLayout.CENTER);
        panelFotos.add(btnAdjuntar, java.awt.BorderLayout.SOUTH);

        // Apilar el formulario (jPanel1) arriba y el panel de fotos abajo
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        getContentPane().add(panelFotos, java.awt.BorderLayout.SOUTH);
        pack();
    }

    private void adjuntarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            for (File f : chooser.getSelectedFiles()) {
                if (!FotoEvidencia.validarFormato(f.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(this,
                            "Archivo no válido: " + f.getName(),
                            "Formato inválido", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                archivosFoto.add(f);
                modeloFotos.addElement(f.getAbsolutePath());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtIDReporte = new javax.swing.JTextField();
        txtIDRecepcion = new javax.swing.JTextField();
        txtIDEquipo = new javax.swing.JTextField();
        txtIDDocente = new javax.swing.JTextField();
        txtEstado = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        txtFalla = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReportesDano = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("REPORTE DE DAÑOS");

        jLabel2.setText("Sistema de Gestion de Laboratorios");

        jLabel3.setText("ID del reporte");

        jLabel4.setText("ID de recepcion");

        jLabel5.setText("ID del Equipo");

        jLabel6.setText("ID del docente");

        jLabel7.setText("Tipo de falla");

        jLabel8.setText("Estado");

        jLabel9.setText("Descripcion del daño");

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(txtIDReporte)
                            .addComponent(txtIDEquipo)
                            .addComponent(txtFalla, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel8)))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(txtIDRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtIDDocente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEstado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnGuardar)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar)
                .addGap(18, 18, 18)
                .addComponent(btnEliminar)
                .addGap(18, 18, 18)
                .addComponent(btnLimpiar)
                .addGap(0, 57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIDRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIDDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(txtFalla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnBuscar)
                    .addComponent(btnEliminar)
                    .addComponent(btnLimpiar))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel10.setText("Tabla de reportes");

        tblReportesDano.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID del reporte", "ID de recepcion", "ID del equipo", "ID del docente", "Tipo de falla", "Descripcion del daño", "Fecha del reporte", "Estado"
            }
        ));
        jScrollPane1.setViewportView(tblReportesDano);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        try {
    int idReporte = Integer.parseInt(txtIDReporte.getText().trim());
    int idRecepcion = Integer.parseInt(txtIDRecepcion.getText().trim());
    int idEquipo = Integer.parseInt(txtIDEquipo.getText().trim());
    int idDocente = Integer.parseInt(txtIDDocente.getText().trim());

    String falla = txtFalla.getText().trim();
    String descripcion = txtDescripcion.getText().trim();
    String estado = txtEstado.getText().trim();

    if (falla.isEmpty() || estado.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el tipo de falla y el estado.");
        return;
    }

    ReporteDano reporte = new ReporteDano(
        idReporte,
        idRecepcion,
        idEquipo,
        idDocente,
        falla,
        descripcion,
        LocalDateTime.now(),
        estado
    );

    // Agregar las fotos de evidencia seleccionadas.
    for (File f : archivosFoto) {
        FotoEvidencia foto = new FotoEvidencia();
        foto.setEquipoid(idEquipo);
        foto.setRutaArchivo(f.getAbsolutePath());
        foto.setDescripcion("Evidencia fotográfica del daño");
        reporte.agregarFoto(foto);
    }

    servicio.registrar(reporte);
    actualizarTabla();

    JOptionPane.showMessageDialog(this,
            "Reporte de daño guardado correctamente con "
            + reporte.getFotos().size() + " foto(s) de evidencia.");

    limpiarCampos();

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this,
            "Los identificadores deben ser números enteros.");
} catch (IllegalArgumentException e) {
    JOptionPane.showMessageDialog(this, e.getMessage());
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
}
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
       try {
    int id = Integer.parseInt(txtIDReporte.getText().trim());

    ReporteDano r = servicio.buscarPorId(id).orElse(null);

    if (r == null) {
        JOptionPane.showMessageDialog(this,
                "No se encontró el reporte.");
        return;
    }

    txtIDRecepcion.setText(String.valueOf(r.getIdRecepcion()));
    txtIDEquipo.setText(String.valueOf(r.getIdEquipo()));
    txtIDDocente.setText(String.valueOf(r.getIdDocente()));
    txtFalla.setText(r.getFalla());
    txtDescripcion.setText(r.getDescripcion());
    txtEstado.setText(r.getEstado());

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this,
            "Ingrese un ID de reporte válido.");
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
} 
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        try {
    int id = Integer.parseInt(txtIDReporte.getText().trim());

    ReporteDano r = servicio.buscarPorId(id).orElse(null);

    if (r == null) {
        JOptionPane.showMessageDialog(this,
                "No se encontró el reporte.");
        return;
    }

    txtIDRecepcion.setText(String.valueOf(r.getIdRecepcion()));
    txtIDEquipo.setText(String.valueOf(r.getIdEquipo()));
    txtIDDocente.setText(String.valueOf(r.getIdDocente()));
    txtFalla.setText(r.getFalla());
    txtDescripcion.setText(r.getDescripcion());
    txtEstado.setText(r.getEstado());

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this,
            "Ingrese un ID de reporte válido.");
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
}
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmReporteDano().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReportesDano;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JTextField txtFalla;
    private javax.swing.JTextField txtIDDocente;
    private javax.swing.JTextField txtIDEquipo;
    private javax.swing.JTextField txtIDRecepcion;
    private javax.swing.JTextField txtIDReporte;
    // End of variables declaration//GEN-END:variables
}
