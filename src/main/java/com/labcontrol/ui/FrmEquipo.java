package com.labcontrol.UI;

import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Laboratorio;
import java.time.LocalDate;

public class FrmEquipo extends javax.swing.JDialog {

    private final EquipoDao equipoDao = new EquipoDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private Equipo original;

    public FrmEquipo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        iniciar(parent);
    }

    
    public static void abrir(java.awt.Frame padre, Equipo e, String tipoInicial) {
        FrmEquipo f = new FrmEquipo(padre, true);
        f.cargar(e, tipoInicial);
        f.setVisible(true);
        f.dispose();
    }

    private void iniciar(java.awt.Frame parent) {
        Ui.estiloEncabezado(lblTitulo);
        Ui.cargarLaboratorios(cboLab);
        txtFecha.setText(LocalDate.now().toString());
        getRootPane().setDefaultButton(btnGuardar);
        Ui.escCierra(this);
        setLocationRelativeTo(parent);
    }

    private void cargar(Equipo e, String tipoInicial) {
        original = e;
        if (e == null) {
            cboTipo.setSelectedItem(tipoInicial == null ? "Otro" : tipoInicial);
            return;
        }
        txtCodigo.setText(e.getCodigoInventario());
        cboTipo.setSelectedItem(e.getTipo());
        txtSerie.setText(e.getNumSerie());
        txtMarca.setText(e.getMarca());
        txtModelo.setText(e.getModelo());
        txtFecha.setText(e.getFechaAdquisicion());
        cboEstado.setSelectedItem(e.getEstado());
        try {
            Laboratorio lab = laboratorioDao.buscarPorId(e.getLaboratorioId());
            if (lab != null) cboLab.setSelectedItem(lab.getCodigo());
        } catch (Exception ex) {
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String tipo = Ui.texto(cboTipo);
        String serie = txtSerie.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String fecha = txtFecha.getText().trim();
        String lab = Ui.texto(cboLab);

        if (codigo.isEmpty() || tipo.isEmpty() || serie.isEmpty() || marca.isEmpty()
                || modelo.isEmpty() || lab.isEmpty()) {
            Ui.error(this, "Complete los campos obligatorios: código, tipo, serie, marca, modelo y laboratorio.");
            return;
        }
        if (tipo.equalsIgnoreCase("Computadora")) {
            Ui.error(this, "Las computadoras se registran con su propio formulario (Nuevo Equipo > Computadora).");
            return;
        }
        if (!Ui.fechaValida(fecha)) {
            Ui.error(this, "La fecha debe tener el formato AAAA-MM-DD.");
            txtFecha.requestFocus();
            return;
        }
        int idActual = original == null ? 0 : original.getId();
        try {
            if (equipoDao.existeCodigo(codigo, idActual)) {
                Ui.error(this, "Ya existe un equipo con el código de inventario " + codigo + ".");
                txtCodigo.requestFocus();
                return;
            }

            Equipo e = new Equipo();
            e.setId(idActual);
            e.setCodigoInventario(codigo);
            e.setTipo(tipo);
            e.setNumSerie(serie);
            e.setMarca(marca);
            e.setModelo(modelo);
            e.setFechaAdquisicion(fecha);
            e.setEstado((String) cboEstado.getSelectedItem());
            Laboratorio labObj = laboratorioDao.obtenerOCrear(lab);
            e.setLaboratorioId(labObj.getId());
            e.setObservaciones("");
            if (idActual == 0) {
                equipoDao.insertar(e);
            } else {
                equipoDao.actualizar(e);
            }
        } catch (Exception ex) {
            Ui.fallo(this, ex.getMessage());
            return;
        }
        Ui.info(this, "Equipo guardado correctamente.");
        dispose();
    }

    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblTipo = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        lblSerie = new javax.swing.JLabel();
        txtSerie = new javax.swing.JTextField();
        lblMarca = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        lblModelo = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        lblFecha = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cboEstado = new javax.swing.JComboBox<>();
        lblLab = new javax.swing.JLabel();
        cboLab = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Equipos");
        setResizable(false);

        pnlEncabezado.setBackground(new java.awt.Color(31, 78, 121));

        lblTitulo.setText("Registro de Equipos");
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

        pnlDatos.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del equipo"));

        lblCodigo.setText("Código de Inventario:");

        lblTipo.setText("Tipo:");

        cboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Impresora", "Proyector", "Switch", "Router", "Otro" }));
        cboTipo.setEditable(true);

        lblSerie.setText("Número de Serie:");

        lblMarca.setText("Marca:");

        lblModelo.setText("Modelo:");

        lblFecha.setText("Fecha de adquisición:");

        lblEstado.setText("Estado:");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "DAÑADO", "EN MANTENIMIENTO", "FUERA DE SERVICIO" }));

        lblLab.setText("Laboratorio:");

        cboLab.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LAB-201", "LAB-204" }));
        cboLab.setEditable(true);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCodigo)
                    .addComponent(lblTipo)
                    .addComponent(lblSerie)
                    .addComponent(lblMarca)
                    .addComponent(lblModelo)
                    .addComponent(lblFecha)
                    .addComponent(lblEstado)
                    .addComponent(lblLab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(cboTipo, 0, 280, Short.MAX_VALUE)
                    .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtModelo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(cboEstado, 0, 280, Short.MAX_VALUE)
                    .addComponent(cboLab, 0, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipo)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSerie)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMarca)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblModelo)
                    .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstado)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLab)
                    .addComponent(cboLab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JComboBox<String> cboLab;
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblLab;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblSerie;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtSerie;
}
