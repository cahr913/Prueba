package com.labcontrol.UI;

import com.labcontrol.dao.*;
import com.labcontrol.modelo.*;

import com.labcontrol.reportes.HojaServicioPdf;
import com.labcontrol.servicio.MantenimientoServicio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class FrmMantenimiento extends javax.swing.JFrame {

    private final MantenimientoServicio servicio = new MantenimientoServicio();
    private final EquipoDao equipoDao = new EquipoDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private final ReporteDanoDao reporteDao = new ReporteDanoDao();
    private final CentroEducativoDao centroDao = new CentroEducativoDao();

    private final Map<Integer, Equipo> equipos = new HashMap<>();
    private final Map<Integer, Usuario> responsables = new HashMap<>();

    private Mantenimiento editado;

    private final JTextField txtNumero = new JTextField(12);
    private final JComboBox<OpcionCombo> cboEquipo = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboResponsable = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboReporte = new JComboBox<>();
    private final JComboBox<String> cboTipo = new JComboBox<>(new String[]{"Preventivo", "Correctivo"});
    private final JComboBox<String> cboEstado = new JComboBox<>(new String[]{
            "PENDIENTE", "EN PROCESO", "FINALIZADO", "CANCELADO"});
    private final JComboBox<String> cboEstadoFinal = new JComboBox<>(Equipo.ESTADOS);
    private final JTextField txtFechaInicio = new JTextField(10);
    private final JTextField txtFechaFin = new JTextField(10);
    private final JTextField txtFalla = new JTextField(18);
    private final JTextField txtResultado = new JTextField(14);
    private final JTextArea txtDiagnostico = new JTextArea(3, 30);
    private final JTextArea txtActividades = new JTextArea(3, 30);
    private final JTextField txtRecomendaciones = new JTextField(18);
    private final JTextField txtObservaciones = new JTextField(18);

    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JTextField txtPiezaNombre = new JTextField(12);
    private final JTextField txtPiezaDescripcion = new JTextField(12);
    private final JComboBox<String> cboPiezaAccion = new JComboBox<>(new String[]{"REPARACION", "REEMPLAZO"});
    private final DefaultTableModel modeloPiezas = new DefaultTableModel(
            new Object[]{"Pieza", "Descripción", "Cantidad", "Acción"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final javax.swing.JTable tblPiezas = new javax.swing.JTable(modeloPiezas);

    private final JButton btnGuardar = new JButton("Guardar nuevo");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnPdf = new JButton("Generar Hoja de Servicio (PDF)");

    private DefaultTableModel modeloTabla;
    private final javax.swing.JTable tblMantenimientos = new javax.swing.JTable();

    public FrmMantenimiento() {
        super("Control de Mantenimiento");
        construir();
        recargarCombos();
        txtFechaInicio.setText(LocalDate.now().toString());
        btnActualizar.setEnabled(false);
        btnPdf.setEnabled(false);
        sugerirNumeroServicio();
    }

    // ---------------- Construccion de la ventana ----------------

    private void construir() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("  Módulo de Mantenimiento de Equipos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(panelFormulario(), BorderLayout.NORTH);
        centro.add(panelPiezas(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnPdf);
        sur.add(botones, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Nº servicio", "Equipo", "Responsable", "Tipo", "Estado",
                "Inicio", "Fin", "Falla reportada"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblMantenimientos.setModel(modeloTabla);
        tblMantenimientos.setRowHeight(24);
        tblMantenimientos.getTableHeader().setReorderingAllowed(false);
        tblMantenimientos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionado();
            }
        });
        JScrollPane scroll = new JScrollPane(tblMantenimientos);
        scroll.setPreferredSize(new Dimension(900, 180));
        sur.add(scroll, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnPdf.addActionListener(e -> generarPdf());
        cboTipo.addActionListener(e -> actualizarVisibilidadPiezas());

        setSize(980, 780);
        setLocationRelativeTo(null);
    }

    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del servicio de mantenimiento"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        agregarCampo(panel, c, 0, 0, "Nº de servicio:", txtNumero);
        agregarCampo(panel, c, 1, 0, "Tipo:", cboTipo);
        agregarCampo(panel, c, 2, 0, "Estado del servicio:", cboEstado);
        agregarCampo(panel, c, 0, 1, "Equipo atendido:", cboEquipo);
        agregarCampo(panel, c, 1, 1, "Responsable:", cboResponsable);
        agregarCampo(panel, c, 2, 1, "Reporte de daño relacionado:", cboReporte);
        agregarCampo(panel, c, 0, 2, "Fecha inicio (AAAA-MM-DD):", txtFechaInicio);
        agregarCampo(panel, c, 1, 2, "Fecha fin (AAAA-MM-DD):", txtFechaFin);
        agregarCampo(panel, c, 2, 2, "Estado final del equipo:", cboEstadoFinal);
        agregarCampo(panel, c, 0, 3, "Falla reportada:", txtFalla);
        agregarCampo(panel, c, 1, 3, "Resultado del servicio:", txtResultado);
        agregarCampo(panel, c, 2, 3, "Recomendaciones:", txtRecomendaciones);

        c.gridx = 0;
        c.gridy = 4;
        panel.add(new JLabel("Diagnóstico técnico:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JScrollPane(txtDiagnostico), c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Actividades realizadas:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JScrollPane(txtActividades), c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Observaciones:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtObservaciones, c);

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int columna, int fila,
                              String etiqueta, java.awt.Component campo) {
        c.gridx = columna * 2;
        c.gridy = fila;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = columna * 2 + 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private JPanel panelPiezas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Piezas reparadas o reemplazadas (obligatorio para finalizar un correctivo)"));

        JPanel captura = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        captura.add(new JLabel("Pieza:"));
        captura.add(txtPiezaNombre);
        captura.add(new JLabel("Descripción:"));
        captura.add(txtPiezaDescripcion);
        captura.add(new JLabel("Cantidad:"));
        captura.add(spnCantidad);
        captura.add(new JLabel("Acción:"));
        captura.add(cboPiezaAccion);
        JButton btnAgregarPieza = new JButton("Agregar pieza");
        JButton btnQuitarPieza = new JButton("Quitar seleccionada");
        captura.add(btnAgregarPieza);
        captura.add(btnQuitarPieza);
        panel.add(captura, BorderLayout.NORTH);

        tblPiezas.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tblPiezas);
        scroll.setPreferredSize(new Dimension(600, 90));
        panel.add(scroll, BorderLayout.CENTER);

        btnAgregarPieza.addActionListener(e -> agregarPieza());
        btnQuitarPieza.addActionListener(e -> quitarPieza());
        return panel;
    }

    // ---------------- Carga de datos ----------------

    private void recargarCombos() {
        equipos.clear();
        cboEquipo.removeAllItems();
        try {
            for (Equipo e : equipoDao.listar()) {
                equipos.put(e.getId(), e);
                cboEquipo.addItem(new OpcionCombo(e.getId(),
                        e.getCodigoInventario() + " | " + e.getTipo() + " " + e.getMarca()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los equipos: " + e.getMessage());
        }

        responsables.clear();
        cboResponsable.removeAllItems();
        try {
            for (Usuario u : usuarioDao.listar()) {
                responsables.put(u.getIdUsuario(), u);
                cboResponsable.addItem(new OpcionCombo(u.getIdUsuario(),
                        u.getNombre() + " " + (u.getApellido() == null ? "" : u.getApellido())
                                + " (" + u.getRol() + ")"));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los usuarios: " + e.getMessage());
        }

        cboReporte.removeAllItems();
        cboReporte.addItem(new OpcionCombo(0, "(Sin reporte relacionado)"));
        try {
            for (com.labcontrol.modelo.ReporteDano r : reporteDao.listar()) {
                Equipo equipo = equipos.get(r.getEquipoid());
                cboReporte.addItem(new OpcionCombo(r.getId(),
                        "Reporte #" + r.getId() + " | "
                                + (equipo != null ? equipo.getCodigoInventario() : "Equipo " + r.getEquipoid())
                                + " | " + r.getTipoFalla()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los reportes de daño: " + e.getMessage());
        }

        if (cboEquipo.getItemCount() > 0) {
            cboEquipo.setSelectedIndex(0);
        }
        if (cboResponsable.getItemCount() > 0) {
            cboResponsable.setSelectedIndex(0);
        }
        cboReporte.setSelectedIndex(0);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Mantenimiento m : servicio.listar()) {
                Equipo equipo = equipos.get(m.getEquipoId());
                Usuario responsable = responsables.get(m.getResponsableId());
                modeloTabla.addRow(new Object[]{
                        m.getId(),
                        m.getNumeroServicio(),
                        equipo != null ? equipo.getCodigoInventario() : "Equipo " + m.getEquipoId(),
                        responsable != null ? responsable.getNombre() : "Responsable " + m.getResponsableId(),
                        m.tipoValorBd(),
                        m.getEstadoTexto(),
                        m.getFechaInicio(),
                        m.getFechaFin(),
                        m.getFallaReportada()
                });
            }
        } catch (SQLException e) {
            Ui.error(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void cargarSeleccionado() {
        int fila = tblMantenimientos.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (int) tblMantenimientos.getValueAt(fila, 0);
        try {
            Mantenimiento m = servicio.buscarPorId(id).orElse(null);
            if (m == null) {
                return;
            }
            editado = m;
            txtNumero.setText(m.getNumeroServicio());
            cboTipo.setSelectedItem(m.tipoValorBd());
            cboEstado.setSelectedItem(m.getEstadoTexto());
            seleccionarPorId(cboEquipo, m.getEquipoId());
            seleccionarPorId(cboResponsable, m.getResponsableId());
            seleccionarPorId(cboReporte, m.getReporteDanoId());
            txtFechaInicio.setText(m.getFechaInicio() != null ? m.getFechaInicio().toString() : "");
            txtFechaFin.setText(m.getFechaFin() != null ? m.getFechaFin().toString() : "");
            txtFalla.setText(m.getFallaReportada());
            txtDiagnostico.setText(m.getDiagnostico());
            txtActividades.setText(m.getActividadesRealizadas());
            txtResultado.setText(m.getResultado());
            txtRecomendaciones.setText(m.getRecomendaciones());
            txtObservaciones.setText(m.getObservaciones());
            if (m.getEstadoFinalEquipo() != null) {
                cboEstadoFinal.setSelectedItem(m.getEstadoFinalEquipo());
            }
            cargarPiezas(m);
            actualizarVisibilidadPiezas();
            btnActualizar.setEnabled(true);
            btnPdf.setEnabled(true);
        } catch (SQLException e) {
            Ui.error(this, "Error al cargar el mantenimiento: " + e.getMessage());
        }
    }

    private void cargarPiezas(Mantenimiento m) {
        modeloPiezas.setRowCount(0);
        m.getPiezas().forEach(p -> modeloPiezas.addRow(new Object[]{
                p.getNombre(), p.getDescripcion(), p.getCantidad(),
                p.getAccion() != null ? p.getAccion().name() : "-"}));
    }

    private void seleccionarPorId(JComboBox<OpcionCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedIndex(0);
    }

    // ---------------- Acciones CRUD ----------------

    private void guardar() {
        Mantenimiento m = crearObjetoDesdeFormulario(true);
        if (m == null) {
            return;
        }
        try {
            servicio.registrar(m);
            recargarCombos();
            limpiar();
            Ui.info(this, "Mantenimiento registrado correctamente. El estado del equipo fue actualizado.");
        } catch (SQLException | IllegalStateException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (editado == null) {
            Ui.aviso(this, "Seleccione un mantenimiento de la tabla para actualizarlo.");
            return;
        }
        Mantenimiento m = crearObjetoDesdeFormulario(false);
        if (m == null) {
            return;
        }
        try {
            servicio.actualizar(m);
            recargarCombos();
            limpiar();
            Ui.info(this, "Mantenimiento actualizado correctamente.");
        } catch (SQLException | IllegalStateException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        int fila = tblMantenimientos.getSelectedRow();
        if (fila < 0) {
            Ui.aviso(this, "Seleccione un mantenimiento de la tabla para eliminarlo.");
            return;
        }
        int id = (int) tblMantenimientos.getValueAt(fila, 0);
        if (!Ui.confirmar(this, "¿Desea eliminar el mantenimiento seleccionado? Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            servicio.eliminar(id);
            recargarCombos();
            limpiar();
            Ui.info(this, "Mantenimiento eliminado correctamente.");
        } catch (SQLException e) {
            Ui.error(this, "No se pudo eliminar: " + e.getMessage()
                    + "\n(Verifique que no tenga registros dependientes.)");
        }
    }

    
    private Mantenimiento crearObjetoDesdeFormulario(boolean nuevo) {
        try {
            String numero = txtNumero.getText().trim();
            if (numero.isEmpty()) {
                Ui.aviso(this, "Ingrese el número de servicio.");
                txtNumero.requestFocus();
                return null;
            }
            if (cboEquipo.getItemCount() == 0 || cboEquipo.getSelectedItem() == null) {
                Ui.aviso(this, "No hay equipos registrados. Registre equipos primero.");
                return null;
            }
            int equipoId = ((OpcionCombo) cboEquipo.getSelectedItem()).getId();
            int responsableId = cboResponsable.getItemCount() > 0
                    ? ((OpcionCombo) cboResponsable.getSelectedItem()).getId() : 0;
            int reporteId = ((OpcionCombo) cboReporte.getSelectedItem()).getId();

            String tipo = (String) cboTipo.getSelectedItem();
            Mantenimiento.Estado estado = Mantenimiento.Estado.valueOf((String) cboEstado.getSelectedItem());

            LocalDate fechaInicio = leerFecha(txtFechaInicio.getText(), "fecha de inicio");
            if (fechaInicio == null) {
                return null;
            }
            LocalDate fechaFin = null;
            if (!txtFechaFin.getText().trim().isEmpty()) {
                fechaFin = leerFecha(txtFechaFin.getText(), "fecha de finalización");
                if (fechaFin == null) {
                    return null;
                }
            }

            // Herencia y polimorfismo: el tipo decidido crea la subclase concreta.
            Mantenimiento m = "Correctivo".equals(tipo)
                    ? new MantenimientoCorrectivo()
                    : new MantenimientoPreventivo();
            if (!nuevo) {
                m.setId(editado != null ? editado.getId() : 0);
            }
            m.setNumeroServicio(numero);
            m.setEquipoId(equipoId);
            m.setReporteDanoId(reporteId);
            m.setResponsableId(responsableId);
            m.setTipoTexto(tipo);
            m.setEstado(estado);
            m.setFechaInicio(fechaInicio);
            m.setFechaFin(fechaFin);
            m.setFallaReportada(txtFalla.getText().trim());
            m.setDiagnostico(txtDiagnostico.getText().trim());
            m.setActividadesRealizadas(txtActividades.getText().trim());
            m.setResultado(txtResultado.getText().trim());
            m.setEstadoFinalEquipo((String) cboEstadoFinal.getSelectedItem());
            m.setRecomendaciones(txtRecomendaciones.getText().trim());
            m.setObservaciones(txtObservaciones.getText().trim());

            // Piezas capturadas en la tabla.
            for (int i = 0; i < modeloPiezas.getRowCount(); i++) {
                String nombre = String.valueOf(modeloPiezas.getValueAt(i, 0)).trim();
                if (nombre.isEmpty()) {
                    continue;
                }
                Pieza p = new Pieza();
                p.setNombre(nombre);
                p.setDescripcion(String.valueOf(modeloPiezas.getValueAt(i, 1)));
                p.setCantidad(Integer.parseInt(String.valueOf(modeloPiezas.getValueAt(i, 2))));
                p.setAccion(com.labcontrol.modelo.Accion.valueOf(
                        String.valueOf(modeloPiezas.getValueAt(i, 3))));
                m.agregarPieza(p);
            }

            // Regla polimorfica: finalizar un correctivo exige piezas.
            if (estado == Mantenimiento.Estado.FINALIZADO) {
                m.finalizar();
            }
            return m;
        } catch (NumberFormatException e) {
            Ui.error(this, "Cantidad de pieza inválida: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Ui.error(this, e.getMessage());
            return null;
        }
    }

    private LocalDate leerFecha(String texto, String nombreCampo) {
        if (!Ui.fechaValida(texto.trim())) {
            Ui.aviso(this, "La " + nombreCampo + " debe tener el formato AAAA-MM-DD.");
            return null;
        }
        return LocalDate.parse(texto.trim());
    }

    // ---------------- Piezas ----------------

    private void agregarPieza() {
        String nombre = txtPiezaNombre.getText().trim();
        if (nombre.isEmpty()) {
            Ui.aviso(this, "Escriba el nombre de la pieza.");
            txtPiezaNombre.requestFocus();
            return;
        }
        modeloPiezas.addRow(new Object[]{
                nombre,
                txtPiezaDescripcion.getText().trim(),
                spnCantidad.getValue(),
                cboPiezaAccion.getSelectedItem()
        });
        txtPiezaNombre.setText("");
        txtPiezaDescripcion.setText("");
        spnCantidad.setValue(1);
        txtPiezaNombre.requestFocus();
    }

    private void quitarPieza() {
        int fila = tblPiezas.getSelectedRow();
        if (fila < 0) {
            Ui.aviso(this, "Seleccione la pieza que desea quitar.");
            return;
        }
        modeloPiezas.removeRow(fila);
    }

    private void actualizarVisibilidadPiezas() {
        boolean correctivo = "Correctivo".equals(cboTipo.getSelectedItem());
        tblPiezas.setEnabled(correctivo);
        tblPiezas.setForeground(correctivo ? java.awt.Color.BLACK : java.awt.Color.GRAY);
    }

    // ---------------- Hoja de servicio en PDF ----------------

    private void generarPdf() {
        int fila = tblMantenimientos.getSelectedRow();
        Mantenimiento m;
        if (editado != null && (fila < 0 || (int) tblMantenimientos.getValueAt(fila, 0) == editado.getId())) {
            m = editado;
        } else {
            Ui.aviso(this, "Seleccione un mantenimiento de la tabla.");
            return;
        }
        try {
            Mantenimiento completo = servicio.buscarPorId(m.getId()).orElse(m);
            Equipo equipo = equipoDao.buscarPorId(completo.getEquipoId());
            Usuario responsable = usuarioDao.buscarPorId(completo.getResponsableId());
            com.labcontrol.modelo.Laboratorio laboratorio = equipo != null
                    ? laboratorioDao.buscarPorId(equipo.getLaboratorioId()) : null;
            List<CentroEducativo> centros = centroDao.listar();
            CentroEducativo centro = centros.isEmpty() ? null : centros.get(0);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar hoja de servicio");
            chooser.setSelectedFile(new File("HojaServicio_" + completo.getNumeroServicio() + ".pdf"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File archivo = chooser.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
                archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
            }
            HojaServicioPdf.generar(archivo, completo, equipo, laboratorio, responsable,
                    completo.getPiezas(), centro);
            Ui.info(this, "Hoja de servicio generada correctamente en:\n" + archivo.getAbsolutePath());
        } catch (Exception e) {
            Ui.error(this, "No se pudo generar el PDF: " + e.getMessage());
        }
    }

    // ---------------- Limpieza ----------------

    private void limpiar() {
        editado = null;
        tblMantenimientos.clearSelection();
        txtNumero.setText("");
        cboTipo.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        if (cboEquipo.getItemCount() > 0) {
            cboEquipo.setSelectedIndex(0);
        }
        if (cboResponsable.getItemCount() > 0) {
            cboResponsable.setSelectedIndex(0);
        }
        cboReporte.setSelectedIndex(0);
        txtFechaInicio.setText(LocalDate.now().toString());
        txtFechaFin.setText("");
        txtFalla.setText("");
        txtDiagnostico.setText("");
        txtActividades.setText("");
        txtResultado.setText("");
        cboEstadoFinal.setSelectedIndex(0);
        txtRecomendaciones.setText("");
        txtObservaciones.setText("");
        modeloPiezas.setRowCount(0);
        btnActualizar.setEnabled(false);
        btnPdf.setEnabled(false);
        sugerirNumeroServicio();
        actualizarVisibilidadPiezas();
    }

    private void sugerirNumeroServicio() {
        try {
            int maximo = 0;
            for (Mantenimiento m : servicio.listar()) {
                String numero = m.getNumeroServicio();
                if (numero != null && numero.startsWith("MS-")) {
                    try {
                        maximo = Math.max(maximo, Integer.parseInt(numero.substring(3)));
                    } catch (NumberFormatException ignorado) {
                        // numeros de servicio con otro formato no afectan la sugerencia
                    }
                }
            }
            txtNumero.setText(String.format("MS-%04d", maximo + 1));
        } catch (SQLException ignorado) {
            // si falla la consulta, el usuario escribe el numero a mano
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMantenimiento().setVisible(true));
    }
}
