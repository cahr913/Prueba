package com.labcontrol.ui.vista;

import com.labcontrol.dao.CentroEducativoDao;
import com.labcontrol.modelo.CentroEducativo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PnlCentros extends javax.swing.JPanel implements Refrescable {

    private final CentroEducativoDao dao = new CentroEducativoDao();
    private final List<CentroEducativo> visibles = new ArrayList<>();
    private DefaultTableModel modelo;
    private CentroEducativo editado;
    private String ultimaBusqueda = "";

    public PnlCentros() {
        initComponents();
        iniciar();
    }

    private void iniciar() {
        Ui.estiloTitulo(lblTitulo);
        Ui.configurarTabla(tblCentros, 90, 280, 280, 130);
        modelo = (DefaultTableModel) tblCentros.getModel();
        refrescar();
    }

    @Override
    public void refrescar() {
        cargarTabla();
        cancelarEdicion();
    }

    private void cargarTabla() {
        ultimaBusqueda = txtBusqueda.getText();
        String texto = ultimaBusqueda.trim().toLowerCase();
        visibles.clear();
        modelo.setRowCount(0);
        try {
            for (CentroEducativo centro : dao.listar()) {
                boolean coincide = texto.isEmpty()
                        || centro.getNombre().toLowerCase().contains(texto)
                        || centro.getCodigo().toLowerCase().contains(texto);
                if (coincide) {
                    visibles.add(centro);
                    modelo.addRow(new Object[]{centro.getCodigo(), centro.getNombre(), centro.getDireccion(),
                        centro.getTelefono()});
                }
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar centros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        editado = null;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
    }

    private void cancelarEdicion() {
        limpiarCampos();
        Ui.habilitar(pnlFormulario, false);
    }

    private void nuevo() {
        tblCentros.clearSelection();
        limpiarCampos();
        Ui.habilitar(pnlFormulario, true);
        txtCodigo.requestFocusInWindow();
    }

    private void editar() {
        int fila = Ui.filaSeleccionada(this, tblCentros);
        if (fila < 0) {
            return;
        }
        editado = visibles.get(fila);
        txtCodigo.setText(editado.getCodigo());
        txtNombre.setText(editado.getNombre());
        txtDireccion.setText(editado.getDireccion());
        txtTelefono.setText(editado.getTelefono());
        Ui.habilitar(pnlFormulario, true);
    }

    private void eliminar() {
        int fila = Ui.filaSeleccionada(this, tblCentros);
        if (fila < 0) {
            return;
        }
        CentroEducativo centro = visibles.get(fila);
        try {
            if (!Ui.confirmar(this, "¿Desea eliminar el centro «" + centro.getNombre() + "»?")) {
                return;
            }
            dao.eliminar(centro.getId());
            if (centro == editado) {
                cancelarEdicion();
            }
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "No se pudo eliminar el centro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (codigo.isEmpty()) {
            Ui.aviso(this, "Ingrese el código del centro.");
            txtCodigo.requestFocusInWindow();
            return;
        }
        if (nombre.isEmpty()) {
            Ui.aviso(this, "Ingrese el nombre del centro.");
            txtNombre.requestFocusInWindow();
            return;
        }
        if (direccion.isEmpty()) {
            Ui.aviso(this, "Ingrese la dirección del centro.");
            txtDireccion.requestFocusInWindow();
            return;
        }
        if (telefono.isEmpty()) {
            Ui.aviso(this, "Ingrese el teléfono del centro.");
            txtTelefono.requestFocusInWindow();
            return;
        }
        if (!telefono.matches("[0-9+() -]{7,20}")) {
            Ui.aviso(this, "El teléfono solo puede contener números, espacios, guiones, paréntesis o +, "
                    + "con una longitud de 7 a 20 caracteres.");
            txtTelefono.requestFocusInWindow();
            return;
        }

        CentroEducativo c = new CentroEducativo();
        c.setCodigo(codigo);
        c.setNombre(nombre);
        c.setDireccion(direccion);
        c.setTelefono(telefono);

        try {
            if (editado == null) {
                // Verificar código único
                for (CentroEducativo existente : dao.listar()) {
                    if (existente.getCodigo().equalsIgnoreCase(codigo)) {
                        Ui.aviso(this, "Ya existe un centro con el código «" + codigo + "».");
                        txtCodigo.requestFocusInWindow();
                        return;
                    }
                }
                dao.insertar(c);
            } else {
                // Verificar código único (exceptuando el actual)
                for (CentroEducativo existente : dao.listar()) {
                    if (existente.getId() != editado.getId()
                            && existente.getCodigo().equalsIgnoreCase(codigo)) {
                        Ui.aviso(this, "Ya existe un centro con el código «" + codigo + "».");
                        txtCodigo.requestFocusInWindow();
                        return;
                    }
                }
                editado.setCodigo(codigo);
                editado.setNombre(nombre);
                editado.setDireccion(direccion);
                editado.setTelefono(telefono);
                dao.actualizar(editado);
            }
            cancelarEdicion();
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblBuscar = new javax.swing.JLabel();
        txtBusqueda = new javax.swing.JTextField();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        scrCentros = new javax.swing.JScrollPane();
        tblCentros = new javax.swing.JTable();
        pnlFormulario = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        lblTitulo.setText("Centros Educativos");

        lblBuscar.setText("Buscar centro (nombre o código):");

        txtBusqueda.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBusquedaCaretUpdate(evt);
            }
        });

        btnNuevo.setText("Nuevo centro");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tblCentros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Dirección", "Teléfono"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });


        scrCentros.setViewportView(tblCentros);

        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Centro Educativo"));
        lblCodigo.setText("Código");



        lblNombre.setText("Nombre");



        lblDireccion.setText("Dirección");



        lblTelefono.setText("Teléfono");



        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });


        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlFormularioLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addContainerGap())
                .addGroup(pnlFormularioLayout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlFormularioLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrCentros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(8, 8, 8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrCentros, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBusquedaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBusquedaCaretUpdate
        if (modelo != null && !txtBusqueda.getText().equals(ultimaBusqueda)) {
            cargarTabla();
        }
    }//GEN-LAST:event_txtBusquedaCaretUpdate

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        nuevo();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        editar();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelarEdicion();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JScrollPane scrCentros;
    private javax.swing.JTable tblCentros;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnCancelar;
    // End of variables declaration//GEN-END:variables
}
