package com.labcontrol.UI;

import com.labcontrol.dao.ComputadoraDao;
import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.InstalacionDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.SoftwareDao;
import com.labcontrol.modelo.Computadora;
import com.labcontrol.modelo.Instalacion;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Software;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class FrmAsistenteInstalacion extends javax.swing.JDialog {

    private final ComputadoraDao compuDao = new ComputadoraDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final SoftwareDao softwareDao = new SoftwareDao();
    private final InstalacionDao instalacionDao = new InstalacionDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();

    private final List<Computadora> equipos = new ArrayList<>();
    private final List<Software> listaSoftware = new ArrayList<>();
    private final List<JCheckBox> checks = new ArrayList<>();
    private boolean cargando = true;

    public FrmAsistenteInstalacion(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        iniciar(parent);
    }

    
    public static void abrir(java.awt.Frame padre) {
        FrmAsistenteInstalacion f = new FrmAsistenteInstalacion(padre, true);
        f.setVisible(true);
        f.dispose();
    }

    private void iniciar(java.awt.Frame parent) {
        Ui.estiloEncabezado(lblTitulo);
        pnlSoftware.removeAll();
        pnlSoftware.setLayout(new BoxLayout(pnlSoftware, BoxLayout.Y_AXIS));
        txtFecha.setText(LocalDate.now().toString());

        cboEquipo.removeAllItems();
        try {
            List<Computadora> todas = equipoDao.listarComputadoras();
            for (Computadora c : todas) {
                if (!"Activo".equalsIgnoreCase(c.getEstado())) continue;
                Laboratorio lab = laboratorioDao.buscarPorId(c.getLaboratorioId());
                equipos.add(c);
                cboEquipo.addItem(c.getHostname() + "  |  IP: " + c.getIp() + "  |  "
                        + (lab == null ? "-" : lab.getCodigo()));
            }
            if (equipos.isEmpty())
                lblAviso.setText("No hay computadoras activas. Regístrelas en Equipos > Nuevo equipo.");
        } catch (Exception ex) {
            Ui.fallo(this, "Error al cargar computadoras: " + ex.getMessage());
        }

        try {
            for (Software s : softwareDao.listar()) {
                if (!"Activo".equalsIgnoreCase(s.getEstado())) continue;
                JCheckBox chk = new JCheckBox();
                listaSoftware.add(s);
                checks.add(chk);
                pnlSoftware.add(chk);
            }
            if (checks.isEmpty())
                pnlSoftware.add(new JLabel("No hay software activo. Regístrelo en Software > Registrar software."));
        } catch (Exception ex) {
            Ui.fallo(this, "Error al cargar software: " + ex.getMessage());
        }

        btnRegistrar.setEnabled(!equipos.isEmpty() && !checks.isEmpty());
        cargando = false;
        actualizarChecks();
        Ui.escCierra(this);
        setLocationRelativeTo(parent);
    }

    private Computadora equipoElegido() {
        int i = cboEquipo.getSelectedIndex();
        return (i >= 0 && i < equipos.size()) ? equipos.get(i) : null;
    }

    
    private void actualizarChecks() {
        Computadora pc = equipoElegido();
        for (int i = 0; i < checks.size(); i++) {
            Software s = listaSoftware.get(i);
            JCheckBox chk = checks.get(i);
            boolean yaInstalado = false;
            if (pc != null) {
                try {
                    yaInstalado = instalacionDao.existe(pc.getId(), s.getId());
                } catch (Exception ex) {
                    yaInstalado = false;
                }
            }
            chk.setText(s.getNombre() + " (v" + s.getVersion() + " - " + s.getLicencia() + ")"
                    + (yaInstalado ? "   - ya instalado" : ""));
            chk.setSelected(yaInstalado);
            chk.setEnabled(pc != null && !yaInstalado);
        }
        pnlSoftware.revalidate();
        pnlSoftware.repaint();
    }

    private void registrar() {
        Computadora pc = equipoElegido();
        if (pc == null) { Ui.error(this, "Seleccione una computadora."); return; }
        String fecha = txtFecha.getText().trim();
        if (fecha.isEmpty() || !Ui.fechaValida(fecha)) {
            Ui.error(this, "La fecha debe tener el formato AAAA-MM-DD.");
            return;
        }
        int cantidad = 0;
        try {
            for (int i = 0; i < checks.size(); i++) {
                JCheckBox chk = checks.get(i);
                if (chk.isEnabled() && chk.isSelected()) {
                    instalacionDao.insertar(
                            new Instalacion(0, pc.getId(), listaSoftware.get(i).getId(), fecha));
                    cantidad++;
                }
            }
        } catch (Exception ex) {
            Ui.fallo(this, ex.getMessage());
            actualizarChecks();
            return;
        }
        if (cantidad == 0) {
            Ui.info(this, "Seleccione al menos un software que aún no esté instalado.");
            return;
        }
        Ui.info(this, cantidad + " instalación(es) registrada(s) en " + pc.getHostname() + ".");
        actualizarChecks();
    }

    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlPaso1 = new javax.swing.JPanel();
        cboEquipo = new javax.swing.JComboBox<>();
        lblAviso = new javax.swing.JLabel();
        pnlPaso2 = new javax.swing.JPanel();
        scrSoftware = new javax.swing.JScrollPane();
        pnlSoftware = new javax.swing.JPanel();
        chkSoftware1 = new javax.swing.JCheckBox();
        chkSoftware2 = new javax.swing.JCheckBox();
        chkSoftware3 = new javax.swing.JCheckBox();
        pnlPaso3 = new javax.swing.JPanel();
        txtFecha = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Asistente de Instalación de Software");
        setResizable(false);

        pnlEncabezado.setBackground(new java.awt.Color(31, 78, 121));

        lblTitulo.setText("LabControl - Asistente de Instalación de Software");
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

        pnlPaso1.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Seleccionar Equipo (Computadora Target)"));

        cboEquipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PC-LAB204-01  |  IP: 192.168.1.50  |  LAB-204" }));
        cboEquipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEquipoActionPerformed(evt);
            }
        });

        lblAviso.setText(" ");

        javax.swing.GroupLayout pnlPaso1Layout = new javax.swing.GroupLayout(pnlPaso1);
        pnlPaso1.setLayout(pnlPaso1Layout);
        pnlPaso1Layout.setHorizontalGroup(
            pnlPaso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPaso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboEquipo, 0, 520, Short.MAX_VALUE)
                    .addComponent(lblAviso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPaso1Layout.setVerticalGroup(
            pnlPaso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAviso)
                .addContainerGap())
        );

        pnlPaso2.setBorder(javax.swing.BorderFactory.createTitledBorder("2. Seleccionar Software a Vincular"));

        chkSoftware1.setText("Apache NetBeans (v21.0 - Open Source)");
        chkSoftware1.setSelected(true);

        chkSoftware2.setText("MySQL Server (v8.0 - Comercial)");
        chkSoftware2.setSelected(true);

        chkSoftware3.setText("Python (v3.11 - Freeware)");

        javax.swing.GroupLayout pnlSoftwareLayout = new javax.swing.GroupLayout(pnlSoftware);
        pnlSoftware.setLayout(pnlSoftwareLayout);
        pnlSoftwareLayout.setHorizontalGroup(
            pnlSoftwareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoftwareLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSoftwareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSoftware1)
                    .addComponent(chkSoftware2)
                    .addComponent(chkSoftware3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSoftwareLayout.setVerticalGroup(
            pnlSoftwareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoftwareLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkSoftware1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSoftware2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSoftware3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scrSoftware.setViewportView(pnlSoftware);

        javax.swing.GroupLayout pnlPaso2Layout = new javax.swing.GroupLayout(pnlPaso2);
        pnlPaso2.setLayout(pnlPaso2Layout);
        pnlPaso2Layout.setHorizontalGroup(
            pnlPaso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrSoftware, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPaso2Layout.setVerticalGroup(
            pnlPaso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrSoftware, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlPaso3.setBorder(javax.swing.BorderFactory.createTitledBorder("3. Fecha de Instalación (AAAA-MM-DD)"));

        javax.swing.GroupLayout pnlPaso3Layout = new javax.swing.GroupLayout(pnlPaso3);
        pnlPaso3.setLayout(pnlPaso3Layout);
        pnlPaso3Layout.setHorizontalGroup(
            pnlPaso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPaso3Layout.setVerticalGroup(
            pnlPaso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaso3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnRegistrar.setText("Registrar Instalación");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPaso1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPaso2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPaso3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRegistrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(pnlPaso1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPaso2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPaso3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnCerrar))
                .addContainerGap())
        );

        pack();
    }

    private void cboEquipoActionPerformed(java.awt.event.ActionEvent evt) {
        if (!cargando) {
            actualizarChecks();
        }
    }

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {
        registrar();
    }

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cboEquipo;
    private javax.swing.JCheckBox chkSoftware1;
    private javax.swing.JCheckBox chkSoftware2;
    private javax.swing.JCheckBox chkSoftware3;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlPaso1;
    private javax.swing.JPanel pnlPaso2;
    private javax.swing.JPanel pnlPaso3;
    private javax.swing.JPanel pnlSoftware;
    private javax.swing.JScrollPane scrSoftware;
    private javax.swing.JTextField txtFecha;
}
