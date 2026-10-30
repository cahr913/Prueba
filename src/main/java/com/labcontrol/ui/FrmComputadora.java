package com.labcontrol.UI;
import com.labcontrol.dao.*;
import com.labcontrol.modelo.*;

import java.time.LocalDate;

public class FrmComputadora extends javax.swing.JDialog {

    private static final String IP_REGEX = 
            "^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$";

    private final ComputadoraDao compuDao = new ComputadoraDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private Computadora original;

    public FrmComputadora(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        iniciar(parent);
    }

    
    public static void abrir(java.awt.Frame padre, Computadora c) {
        FrmComputadora f = new FrmComputadora(padre, true);
        f.cargar(c);
        f.setVisible(true);
        f.dispose();
    }

    private void iniciar(java.awt.Frame parent) {
        Ui.estiloEncabezado(lblTitulo);
        javax.swing.ButtonGroup grupo = new javax.swing.ButtonGroup();
        grupo.add(rbArq64);
        grupo.add(rbArq32);
        Ui.cargarLaboratorios(cboLab);
        txtFecha.setText(LocalDate.now().toString());
        getRootPane().setDefaultButton(btnGuardar);
        Ui.escCierra(this);
        setLocationRelativeTo(parent);
    }

    private void cargar(Computadora c) {
        original = c;
        if (c == null) return;
        txtCodigo.setText(c.getCodigoInventario());
        txtSerie.setText(c.getNumSerie());
        txtMarca.setText(c.getMarca());
        txtModelo.setText(c.getModelo());
        txtFecha.setText(c.getFechaAdquisicion());
        cboEstado.setSelectedItem(c.getEstado());
        try {
            Laboratorio lab = laboratorioDao.buscarPorId(c.getLaboratorioId());
            if (lab != null) cboLab.setSelectedItem(lab.getCodigo());
        } catch (Exception ex) {
        }
        txtHost.setText(c.getHostname());
        txtIp.setText(c.getIp());
        cboSO.setSelectedItem(c.getSistemaOperativo());
        if ("32-bits".equals(c.getArquitectura())) rbArq32.setSelected(true); else rbArq64.setSelected(true);
        txtCaract.setText(c.getCaracteristicas());
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String serie = txtSerie.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String fecha = txtFecha.getText().trim();
        String host = txtHost.getText().trim();
        String ip = txtIp.getText().trim();
        String so = Ui.texto(cboSO);
        String lab = Ui.texto(cboLab);

        if (codigo.isEmpty() || serie.isEmpty() || marca.isEmpty() || modelo.isEmpty()
                || host.isEmpty() || lab.isEmpty()) {
            Ui.error(this, "Complete los campos obligatorios: código, serie, marca, modelo, host name y laboratorio.");
            return;
        }
        if (!ip.matches(IP_REGEX)) {
            Ui.error(this, "La dirección IP no es válida (ejemplo: 192.168.1.50).");
            txtIp.requestFocus();
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

            String labCodigo = Ui.texto(cboLab);
            if (!labCodigo.isEmpty()) {
                Laboratorio labSel = laboratorioDao.buscarPorCodigo(labCodigo);
                if (labSel != null && labSel.getCapacidad() > 0) {
                    int count = 0;
                    for (Computadora comp : compuDao.listar()) {
                        if (comp.getLaboratorioId() == labSel.getId()) {
                            count++;
                        }
                    }
                    if (idActual == 0 && count >= labSel.getCapacidad()) {
                        Ui.error(this, "El laboratorio «" + labSel.getNombre()
                                + "» ya alcanzó su capacidad máxima de "
                                + labSel.getCapacidad() + " computadoras.");
                        return;
                    }
                    if (idActual > 0 && original != null
                            && original.getLaboratorioId() != labSel.getId()
                            && count >= labSel.getCapacidad()) {
                        Ui.error(this, "El laboratorio «" + labSel.getNombre()
                                + "» ya alcanzó su capacidad máxima de "
                                + labSel.getCapacidad() + " computadoras.");
                        return;
                    }
                }
            }

            Computadora c = new Computadora();
            c.setId(idActual);
            c.setCodigoInventario(codigo);
            c.setNumSerie(serie);
            c.setMarca(marca);
            c.setModelo(modelo);
            c.setFechaAdquisicion(fecha);
            c.setEstado((String) cboEstado.getSelectedItem());
            Laboratorio labObj = laboratorioDao.obtenerOCrear(lab);
            c.setLaboratorioId(labObj.getId());
            c.setHostname(host);
            c.setIp(ip);
            c.setSistemaOperativo(so);
            c.setArquitectura(rbArq64.isSelected() ? "64-bits" : "32-bits");
            c.setCaracteristicas(txtCaract.getText().trim());
            c.setObservaciones("");
            if (idActual == 0) {
                compuDao.insertar(c);
            } else {
                compuDao.actualizar(c);
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
        pnlGeneral = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
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
        pnlTecnica = new javax.swing.JPanel();
        lblHost = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        lblIp = new javax.swing.JLabel();
        txtIp = new javax.swing.JTextField();
        lblSO = new javax.swing.JLabel();
        cboSO = new javax.swing.JComboBox<>();
        lblArq = new javax.swing.JLabel();
        rbArq64 = new javax.swing.JRadioButton();
        rbArq32 = new javax.swing.JRadioButton();
        lblCaract = new javax.swing.JLabel();
        txtCaract = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Computadoras");
        setResizable(false);

        pnlEncabezado.setBackground(new java.awt.Color(31, 78, 121));

        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Registro de Computadoras");

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

        pnlGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del equipo"));

        lblCodigo.setText("Código de Inventario:");

        lblSerie.setText("Número de Serie:");

        lblMarca.setText("Marca:");

        lblModelo.setText("Modelo:");

        lblFecha.setText("Fecha de adquisición:");

        lblEstado.setText("Estado:");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "DAÑADO", "EN MANTENIMIENTO", "FUERA DE SERVICIO" }));

        lblLab.setText("Laboratorio:");

        cboLab.setEditable(true);
        cboLab.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LAB-201", "LAB-204" }));

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCodigo)
                    .addComponent(lblSerie)
                    .addComponent(lblMarca)
                    .addComponent(lblModelo)
                    .addComponent(lblFecha)
                    .addComponent(lblEstado)
                    .addComponent(lblLab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtModelo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(cboEstado, 0, 280, Short.MAX_VALUE)
                    .addComponent(cboLab, 0, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSerie)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMarca)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblModelo)
                    .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstado)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLab)
                    .addComponent(cboLab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlTecnica.setBorder(javax.swing.BorderFactory.createTitledBorder("CARACTERÍSTICAS TÉCNICAS ESPECÍFICAS"));

        lblHost.setText("Host Name:");

        lblIp.setText("Dirección IP:");

        txtIp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIpActionPerformed(evt);
            }
        });

        lblSO.setText("Sistema Operativo:");

        cboSO.setEditable(true);
        cboSO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Windows 11 Pro", "Windows 10 Pro", "Ubuntu 24.04 LTS", "Ubuntu 22.04 LTS", "Debian 12" }));

        lblArq.setText("Arquitectura:");

        rbArq64.setSelected(true);
        rbArq64.setText("64-bits");

        rbArq32.setText("32-bits");

        lblCaract.setText("Especificaciones (RAM):");

        javax.swing.GroupLayout pnlTecnicaLayout = new javax.swing.GroupLayout(pnlTecnica);
        pnlTecnica.setLayout(pnlTecnicaLayout);
        pnlTecnicaLayout.setHorizontalGroup(
            pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTecnicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblHost)
                    .addComponent(lblIp)
                    .addComponent(lblSO)
                    .addComponent(lblArq)
                    .addComponent(lblCaract))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtHost, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtIp, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(cboSO, 0, 280, Short.MAX_VALUE)
                    .addGroup(pnlTecnicaLayout.createSequentialGroup()
                        .addComponent(rbArq64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbArq32))
                    .addComponent(txtCaract, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTecnicaLayout.setVerticalGroup(
            pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTecnicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHost)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIp)
                    .addComponent(txtIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSO)
                    .addComponent(cboSO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblArq)
                    .addComponent(rbArq64)
                    .addComponent(rbArq32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCaract)
                    .addComponent(txtCaract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTecnica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTecnica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void txtIpActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<String> cboLab;
    private javax.swing.JComboBox<String> cboSO;
    private javax.swing.JLabel lblArq;
    private javax.swing.JLabel lblCaract;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblIp;
    private javax.swing.JLabel lblLab;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblSO;
    private javax.swing.JLabel lblSerie;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlTecnica;
    private javax.swing.JRadioButton rbArq32;
    private javax.swing.JRadioButton rbArq64;
    private javax.swing.JTextField txtCaract;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtIp;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtSerie;
}
