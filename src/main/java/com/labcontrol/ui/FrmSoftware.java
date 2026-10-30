package com.labcontrol.UI;

import com.labcontrol.dao.SoftwareDao;
import com.labcontrol.modelo.Software;

public class FrmSoftware extends javax.swing.JDialog {

    private final SoftwareDao softwareDao = new SoftwareDao();
    private Software original;

    public FrmSoftware(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        iniciar(parent);
    }

    
    public static void abrir(java.awt.Frame padre, Software s) {
        FrmSoftware f = new FrmSoftware(padre, true);
        f.cargar(s);
        f.setVisible(true);
        f.dispose();
    }

    private void iniciar(java.awt.Frame parent) {
        Ui.estiloEncabezado(lblTitulo);
        getRootPane().setDefaultButton(btnGuardar);
        Ui.escCierra(this);
        setLocationRelativeTo(parent);
    }

    private void cargar(Software s) {
        original = s;
        setTitle(s == null ? "Registrar Software" : "Editar Software");
        if (s == null) return;
        txtNombre.setText(s.getNombre());
        txtVersion.setText(s.getVersion());
        cboTipo.setSelectedItem(s.getTipo());
        cboLicencia.setSelectedItem(s.getLicencia());
        cboEstado.setSelectedItem(s.getEstado());
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String version = txtVersion.getText().trim();
        if (nombre.isEmpty() || version.isEmpty()) {
            Ui.error(this, "Nombre y versión son obligatorios.");
            return;
        }
        int idActual = original == null ? 0 : original.getId();
        try {
            if (softwareDao.existeNombreVersion(nombre, version, idActual)) {
                Ui.error(this, "Ya existe " + nombre + " en la versión " + version + ".");
                return;
            }
            Software s = new Software();
            s.setId(idActual);
            s.setNombre(nombre);
            s.setVersion(version);
            s.setTipo((String) cboTipo.getSelectedItem());
            s.setLicencia((String) cboLicencia.getSelectedItem());
            s.setEstado((String) cboEstado.getSelectedItem());
            if (idActual == 0) {
                softwareDao.insertar(s);
            } else {
                softwareDao.actualizar(s);
            }
        } catch (Exception ex) {
            Ui.fallo(this, ex.getMessage());
            return;
        }
        dispose();
    }

    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblTipo = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        lblLicencia = new javax.swing.JLabel();
        cboLicencia = new javax.swing.JComboBox<>();
        lblEstado = new javax.swing.JLabel();
        cboEstado = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registrar Software");
        setResizable(false);

        pnlEncabezado.setBackground(new java.awt.Color(31, 78, 121));

        lblTitulo.setText("Catálogo de Software y Licencias");
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitulo)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTitulo)
                .addGap(12, 12, 12))
        );

        pnlDatos.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del software"));

        lblNombre.setText("Nombre:");

        lblVersion.setText("Versión:");

        lblTipo.setText("Tipo:");

        cboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aplicacion", "Sistema" }));

        lblLicencia.setText("Licencia:");

        cboLicencia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Open Source", "Comercial", "Freeware" }));

        lblEstado.setText("Estado:");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNombre)
                    .addComponent(lblVersion)
                    .addComponent(lblTipo)
                    .addComponent(lblLicencia)
                    .addComponent(lblEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(txtVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(cboTipo, 0, 240, Short.MAX_VALUE)
                    .addComponent(cboLicencia, 0, 240, Short.MAX_VALUE)
                    .addComponent(cboEstado, 0, 240, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVersion)
                    .addComponent(txtVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipo)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLicencia)
                    .addComponent(cboLicencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstado)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        pack();
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        guardar();
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<String> cboLicencia;
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblLicencia;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtVersion;
}
