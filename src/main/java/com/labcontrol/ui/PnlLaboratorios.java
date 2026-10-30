package com.labcontrol.UI;

import com.labcontrol.dao.CentroEducativoDao;
import com.labcontrol.dao.ComputadoraDao;
import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.modelo.CentroEducativo;
import com.labcontrol.modelo.Computadora;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Laboratorio;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PnlLaboratorios extends javax.swing.JPanel implements Refrescable {

    private final CentroEducativoDao centroDao = new CentroEducativoDao();
    private final LaboratorioDao dao = new LaboratorioDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final ComputadoraDao compuDao = new ComputadoraDao();
    private final List<Laboratorio> visibles = new ArrayList<>();
    private final List<CentroEducativo> centros = new ArrayList<>();
    private DefaultTableModel modelo;
    private Laboratorio editado;
    private boolean cargando = true;

    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloComputadoras;
    private JTable tblEquipos;
    private JTable tblComputadoras;
    private JTabbedPane pnlTabsEquipos;

    public PnlLaboratorios() {
        initComponents();
        crearPanelesEquipos();
        iniciar();
    }

    private void iniciar() {
        Ui.estiloTitulo(lblTitulo);
        Ui.configurarTabla(tblLaboratorios, 80, 220, 80, 220, 120, 200);
        modelo = (DefaultTableModel) tblLaboratorios.getModel();
        refrescar();

        tblLaboratorios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tblLaboratorios.getSelectedRow();
                if (fila >= 0) {
                    Laboratorio lab = visibles.get(fila);
                    cargarEquiposYComputadoras(lab.getId());
                } else {
                    limpiarEquiposComputadoras();
                }
            }
        });
    }

    private void crearPanelesEquipos() {
        pnlTabsEquipos = new JTabbedPane();

        JPanel pnlEquipos = new JPanel(new java.awt.BorderLayout());
        tblEquipos = new JTable();
        modeloEquipos = new DefaultTableModel(
            new Object[]{"Código", "Tipo", "Marca", "Modelo", "Serie", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblEquipos.setModel(modeloEquipos);
        Ui.configurarTabla(tblEquipos, 100, 100, 120, 120, 120, 100);
        pnlEquipos.add(new JScrollPane(tblEquipos), java.awt.BorderLayout.CENTER);
        pnlTabsEquipos.addTab("Equipos", pnlEquipos);

        JPanel pnlComputadoras = new JPanel(new java.awt.BorderLayout());
        tblComputadoras = new JTable();
        modeloComputadoras = new DefaultTableModel(
            new Object[]{"Código", "Host Name", "IP", "Sistema Operativo", "Arquitectura", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblComputadoras.setModel(modeloComputadoras);
        Ui.configurarTabla(tblComputadoras, 100, 120, 100, 150, 80, 100);
        pnlComputadoras.add(new JScrollPane(tblComputadoras), java.awt.BorderLayout.CENTER);
        pnlTabsEquipos.addTab("Computadoras", pnlComputadoras);

        pnlTabsEquipos.setVisible(false);

        javax.swing.GroupLayout layout = (javax.swing.GroupLayout) getLayout();

        javax.swing.GroupLayout.SequentialGroup hSeq = layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap();
        javax.swing.GroupLayout.ParallelGroup hPar = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        hPar.addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        hPar.addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(lblFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cboFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16)
            .addComponent(lblFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap());
        hPar.addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(scrLaboratorios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap());
        hPar.addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap());
        hPar.addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(pnlTabsEquipos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap());
        layout.setHorizontalGroup(hPar);

        javax.swing.GroupLayout.SequentialGroup vSeq = layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(8, 8, 8)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(scrLaboratorios, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(pnlTabsEquipos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
            .addContainerGap();
        layout.setVerticalGroup(vSeq);

        add(pnlTabsEquipos);
    }

    private void cargarEquiposYComputadoras(int laboratorioId) {
        try {
            modeloEquipos.setRowCount(0);
            modeloComputadoras.setRowCount(0);

            int countEquipos = 0;
            int countComputadoras = 0;

            for (Equipo e : equipoDao.listar()) {
                if (e.getLaboratorioId() == laboratorioId) {
                    modeloEquipos.addRow(new Object[]{
                        e.getCodigoInventario(), e.getTipo(), e.getMarca(),
                        e.getModelo(), e.getNumSerie(), e.getEstado()
                    });
                    countEquipos++;
                }
            }

            for (Computadora c : compuDao.listar()) {
                if (c.getLaboratorioId() == laboratorioId) {
                    modeloComputadoras.addRow(new Object[]{
                        c.getCodigoInventario(), c.getHostname(), c.getIp(),
                        c.getSistemaOperativo(), c.getArquitectura(), c.getEstado()
                    });
                    countComputadoras++;
                }
            }

            pnlTabsEquipos.setVisible(true);
            revalidate();
            repaint();

            System.out.println("Laboratorio " + laboratorioId + ": " + countEquipos + " equipos, " + countComputadoras + " computadoras");

            if (countEquipos == 0 && countComputadoras == 0) {
                Ui.aviso(this, "No hay equipos ni computadoras registrados en este laboratorio.");
            }
        } catch (SQLException ex) {
            Ui.fallo(this, "Error al cargar equipos y computadoras: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarEquiposComputadoras() {
        modeloEquipos.setRowCount(0);
        modeloComputadoras.setRowCount(0);
        pnlTabsEquipos.setVisible(false);
        revalidate();
        repaint();
    }

    @Override
    public void refrescar() {
        cargando = true;
        try {
            cargarCentros();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar centros: " + e.getMessage());
            e.printStackTrace();
        }
        cargando = false;
        cargarTabla();
        cancelarEdicion();
        limpiarEquiposComputadoras();
    }

    private void cargarCentros() throws SQLException {
        centros.clear();
        centros.addAll(centroDao.listar());
        cboFiltroCentro.removeAllItems();
        cboCentro.removeAllItems();
        cboFiltroCentro.addItem("<Todos>");
        cboCentro.addItem("<Seleccione>");
        for (CentroEducativo c : centros) {
            cboFiltroCentro.addItem(c);
            cboCentro.addItem(c);
        }
    }

    private void cargarTabla() {
        Object centroFiltro = cboFiltroCentro.getSelectedItem();
        Object estadoFiltro = cboFiltroEstado.getSelectedItem();
        visibles.clear();
        modelo.setRowCount(0);
        try {
            for (Laboratorio laboratorio : dao.listar()) {
                boolean coincideCentro = !(centroFiltro instanceof CentroEducativo)
                        || laboratorio.getCentroId() == ((CentroEducativo) centroFiltro).getId();
                boolean coincideEstado = "Todos".equals(estadoFiltro) || laboratorio.getEstado().equals(estadoFiltro);
                if (coincideCentro && coincideEstado) {
                    visibles.add(laboratorio);
                    String centroNombre = "";
                    for (CentroEducativo c : centros) {
                        if (c.getId() == laboratorio.getCentroId()) {
                            centroNombre = c.getNombre();
                            break;
                        }
                    }
                    modelo.addRow(new Object[]{laboratorio.getCodigo(), laboratorio.getNombre(),
                        laboratorio.getCapacidad(), laboratorio.getUbicacion(), laboratorio.getEstado(),
                        centroNombre});
                }
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar laboratorios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        editado = null;
        tblLaboratorios.clearSelection();
        txtNombre.setText("");
        txtCapacidad.setText("");
        txtUbicacion.setText("");
        cboEstado.setSelectedIndex(0);
        if (cboCentro.getItemCount() > 0) {
            cboCentro.setSelectedIndex(0);
        }
        limpiarEquiposComputadoras();
    }

    private void cancelarEdicion() {
        limpiarCampos();
        Ui.habilitar(pnlFormulario, false);
    }

    private void nuevo() {
        if (centros.isEmpty()) {
            Ui.aviso(this, "Registre al menos un centro educativo antes de crear laboratorios.");
            return;
        }
        tblLaboratorios.clearSelection();
        limpiarCampos();
        Ui.habilitar(pnlFormulario, true);
        txtNombre.requestFocusInWindow();
    }

    private void editar() {
        int fila = Ui.filaSeleccionada(this, tblLaboratorios);
        if (fila < 0) {
            return;
        }
        editado = visibles.get(fila);
        txtNombre.setText(editado.getNombre());
        txtCapacidad.setText(String.valueOf(editado.getCapacidad()));
        txtUbicacion.setText(editado.getUbicacion());
        cboEstado.setSelectedItem(editado.getEstado());
        for (int i = 0; i < cboCentro.getItemCount(); i++) {
            Object item = cboCentro.getItemAt(i);
            if (item instanceof CentroEducativo
                    && ((CentroEducativo) item).getId() == editado.getCentroId()) {
                cboCentro.setSelectedIndex(i);
                break;
            }
        }
        Ui.habilitar(pnlFormulario, true);
    }

    private void eliminar() {
        int fila = Ui.filaSeleccionada(this, tblLaboratorios);
        if (fila < 0) {
            return;
        }
        Laboratorio laboratorio = visibles.get(fila);
        try {
            if (!Ui.confirmar(this, "¿Desea eliminar el laboratorio «" + laboratorio.getNombre() + "»?")) {
                return;
            }
            dao.eliminar(laboratorio.getId());
            if (laboratorio == editado) {
                cancelarEdicion();
            }
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "No se pudo eliminar el laboratorio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        String estado = (String) cboEstado.getSelectedItem();
        CentroEducativo centro = (CentroEducativo) cboCentro.getSelectedItem();

        if (nombre.isEmpty()) {
            Ui.aviso(this, "Ingrese el nombre del laboratorio.");
            txtNombre.requestFocusInWindow();
            return;
        }
        int capacidad = leerEnteroPositivo(txtCapacidad.getText());
        if (capacidad <= 0) {
            Ui.aviso(this, "La capacidad debe ser un número entero positivo.");
            txtCapacidad.requestFocusInWindow();
            return;
        }
        if (ubicacion.isEmpty()) {
            Ui.aviso(this, "Ingrese la ubicación del laboratorio.");
            txtUbicacion.requestFocusInWindow();
            return;
        }
        if (centro == null || centro.getId() <= 0) {
            Ui.aviso(this, "Seleccione el centro educativo al que pertenece el laboratorio.");
            return;
        }

        Laboratorio l = new Laboratorio();
        l.setNombre(nombre);
        l.setUbicacion(ubicacion);
        l.setCapacidad(capacidad);
        l.setEstado(estado);
        l.setResponsable("");
        l.setObservaciones("");
        l.setCentroId(centro.getId());

        try {
            if (editado == null) {
                dao.insertar(l);
            } else {
                l.setId(editado.getId());
                dao.actualizar(l);
            }
            cancelarEdicion();
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int leerEnteroPositivo(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblFiltroCentro = new javax.swing.JLabel();
        cboFiltroCentro = new javax.swing.JComboBox<>();
        lblFiltroEstado = new javax.swing.JLabel();
        cboFiltroEstado = new javax.swing.JComboBox<>();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        scrLaboratorios = new javax.swing.JScrollPane();
        tblLaboratorios = new javax.swing.JTable();
        pnlFormulario = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblCapacidad = new javax.swing.JLabel();
        txtCapacidad = new javax.swing.JTextField();
        lblUbicacion = new javax.swing.JLabel();
        txtUbicacion = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cboEstado = new javax.swing.JComboBox<>();
        lblCentro = new javax.swing.JLabel();
        cboCentro = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        lblTitulo.setText("Laboratorios");

        lblFiltroCentro.setText("Centro:");

        cboFiltroCentro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroCentroActionPerformed(evt);
            }
        });

        lblFiltroEstado.setText("Estado:");

        cboFiltroEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo", "En mantenimiento" }));
        cboFiltroEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroEstadoActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo laboratorio");
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

        tblLaboratorios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Capacidad", "Ubicación", "Estado", "Centro"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return canEdit [columnIndex];

            }
        });
        scrLaboratorios.setViewportView(tblLaboratorios);

        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Laboratorio"));

        lblNombre.setText("Nombre");

        lblCapacidad.setText("Capacidad");

        lblUbicacion.setText("Ubicación");

        lblEstado.setText("Estado");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo", "En mantenimiento" }));

        lblCentro.setText("Centro");

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
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNombre)
                            .addComponent(txtNombre))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCapacidad)
                            .addComponent(txtCapacidad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUbicacion)
                            .addComponent(txtUbicacion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEstado)
                            .addComponent(cboEstado, 0, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCentro)
                            .addComponent(cboCentro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(lblCapacidad)
                    .addComponent(lblUbicacion)
                    .addComponent(lblEstado)
                    .addComponent(lblCentro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFiltroCentro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(lblFiltroEstado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar))
                            .addComponent(scrLaboratorios)
                            .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFiltroCentro)
                    .addComponent(cboFiltroCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFiltroEstado)
                    .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrLaboratorios, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    private void cboFiltroCentroActionPerformed(java.awt.event.ActionEvent evt) {
        if (!cargando) {
            cargarTabla();
        }
    }

    private void cboFiltroEstadoActionPerformed(java.awt.event.ActionEvent evt) {
        if (!cargando) {
            cargarTabla();
        }
    }

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {
        nuevo();
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        editar();
    }

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        eliminar();
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        guardar();
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        cancelarEdicion();
    }

    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<Object> cboCentro;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<Object> cboFiltroCentro;
    private javax.swing.JComboBox<String> cboFiltroEstado;
    private javax.swing.JLabel lblCapacidad;
    private javax.swing.JLabel lblCentro;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFiltroCentro;
    private javax.swing.JLabel lblFiltroEstado;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JScrollPane scrLaboratorios;
    private javax.swing.JTable tblLaboratorios;
    private javax.swing.JTextField txtCapacidad;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtUbicacion;
}
