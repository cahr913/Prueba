package com.labcontrol.ui.vista;

import com.labcontrol.dao.ComputadoraDao;
import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.modelo.Computadora;
import com.labcontrol.modelo.Laboratorio;
import java.time.LocalDate;

/**
 * Pantalla 2: Registro de Computadoras (alta y edición).
 * Se edita visualmente desde la pestaña "Design" de NetBeans.
 */
public class FrmComputadora extends javax.swing.JDialog {

    private static final String IP_REGEX =
            "^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$";

    private final ComputadoraDao compuDao = new ComputadoraDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private Computadora original;   // null = registro nuevo

    public FrmComputadora(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        iniciar(parent);
    }

    /** Abre el formulario (modal). Con c == null registra una computadora nueva. */
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
            // ignorar error de laboratorio en cargar
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

            // Validar capacidad del laboratorio (solo computadoras)
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
                    // Si es nuevo registro, validar capacidad
                    if (idActual == 0 && count >= labSel.getCapacidad()) {
                        Ui.error(this, "El laboratorio «" + labSel.getNombre()
                                + "» ya alcanzó su capacidad máxima de "
                                + labSel.getCapacidad() + " computadoras.");
                        return;
                    }
                    // Si es edición y se cambia de laboratorio, validar el nuevo
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        lblTitulo.setText("Registro de Computadoras");
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12))
        );


        pnlGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del equipo"));
        lblCodigo.setText("Código de Inventario:");


        lblSerie.setText("Número de Serie:");


        lblMarca.setText("Marca:");


        lblModelo.setText("Modelo:");


        lblFecha.setText("Fecha de adquisición:");


        lblEstado.setText("Estado:");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "DAÑADO", "EN MANTENIMIENTO" }));

        lblLab.setText("Laboratorio:");

        cboLab.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LAB-201", "LAB-204" }));
        cboLab.setEditable(true);

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlGeneralLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(txtModelo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(cboEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(cboLab, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                    .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlGeneralLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );


        pnlTecnica.setBorder(javax.swing.BorderFactory.createTitledBorder("CARACTERÍSTICAS TÉCNICAS ESPECÍFICAS"));
        lblHost.setText("Host Name:");


        lblIp.setText("Dirección IP:");


        lblSO.setText("Sistema Operativo:");

        cboSO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Windows 11 Pro", "Windows 10 Pro", "Ubuntu 24.04 LTS", "Ubuntu 22.04 LTS", "Debian 12" }));
        cboSO.setEditable(true);

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
                        .addComponent(lblHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblArq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCaract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtHost, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(txtIp, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(cboSO, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addGroup(pnlTecnicaLayout.createSequentialGroup()
                            .addComponent(rbArq64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rbArq32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtCaract, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                    .addContainerGap())
        );
        pnlTecnicaLayout.setVerticalGroup(
            pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlTecnicaLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblArq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rbArq64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rbArq32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlTecnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCaract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlTecnica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JLabel lblSerie;
    private javax.swing.JTextField txtSerie;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JLabel lblLab;
    private javax.swing.JComboBox<String> cboLab;
    private javax.swing.JPanel pnlTecnica;
    private javax.swing.JLabel lblHost;
    private javax.swing.JTextField txtHost;
    private javax.swing.JLabel lblIp;
    private javax.swing.JTextField txtIp;
    private javax.swing.JLabel lblSO;
    private javax.swing.JComboBox<String> cboSO;
    private javax.swing.JLabel lblArq;
    private javax.swing.JRadioButton rbArq64;
    private javax.swing.JRadioButton rbArq32;
    private javax.swing.JLabel lblCaract;
    private javax.swing.JTextField txtCaract;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnCancelar;
    // End of variables declaration//GEN-END:variables
}