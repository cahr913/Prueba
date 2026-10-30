package com.labcontrol.UI;

import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.FotoEvidencia;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.ReporteDano;
import com.labcontrol.modelo.Usuario;
import com.labcontrol.servicio.ReporteDanoServicio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmReporteDano extends javax.swing.JFrame {

    private static final String[] TIPOS_FALLA = {
            "No enciende", "Pantalla dañada", "Teclado defectuoso", "Ratón defectuoso",
            "No da video", "Sin conexión de red", "Falla de software", "Ruido anormal", "Otra falla"
    };
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReporteDanoServicio servicio = new ReporteDanoServicio();
    private final EquipoDao equipoDao = new EquipoDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();

    private final Map<Integer, Equipo> equipos = new HashMap<>();
    private final Map<Integer, Laboratorio> laboratorios = new HashMap<>();
    private final Map<Integer, Usuario> docentes = new HashMap<>();

    private ReporteDano editado;

    private final JComboBox<OpcionCombo> cboLaboratorio = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboEquipo = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboDocente = new JComboBox<>();
    private final JComboBox<String> cboTipoFalla = new JComboBox<>(TIPOS_FALLA);
    private final JTextField txtFechaHora = new JTextField(16);
    private final JComboBox<String> cboEstadoEncontrado =
            new JComboBox<>(new String[]{"DAÑADO", "EN MANTENIMIENTO", "FUERA DE SERVICIO"});
    private final JTextArea txtDescripcion = new JTextArea(3, 30);
    private final JTextField txtObservaciones = new JTextField(20);

    private final DefaultListModel<String> modeloFotos = new DefaultListModel<>();
    private final JList<String> lstFotos = new JList<>(modeloFotos);
    private final JButton btnAgregarFoto = new JButton("Agregar fotografía...");
    private final JButton btnQuitarFoto = new JButton("Quitar seleccionada");

    private final JButton btnGuardar = new JButton("Guardar nuevo reporte");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private DefaultTableModel modeloTabla;
    private final javax.swing.JTable tblReportes = new javax.swing.JTable();

    public FrmReporteDano() {
        super("Reporte de Equipos Dañados");
        construir();
        recargarCombos();
        btnActualizar.setEnabled(false);
    }

    // ---------------- Construccion ----------------

    private void construir() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("  Reporte de Equipos Dañados");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(panelFormulario(), BorderLayout.NORTH);
        centro.add(panelFotos(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        sur.add(botones, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Fecha del reporte", "Laboratorio", "Equipo", "Docente",
                "Tipo de falla", "Estado", "Fotos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblReportes.setModel(modeloTabla);
        tblReportes.setRowHeight(24);
        tblReportes.getTableHeader().setReorderingAllowed(false);
        tblReportes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionado();
            }
        });
        JScrollPane scroll = new JScrollPane(tblReportes);
        scroll.setPreferredSize(new Dimension(950, 170));
        sur.add(scroll, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnAgregarFoto.addActionListener(e -> agregarFoto());
        btnQuitarFoto.addActionListener(e -> quitarFoto());
        cboLaboratorio.addActionListener(e -> cargarEquiposDelLaboratorio());

        setSize(1000, 720);
        setLocationRelativeTo(null);
    }

    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del daño encontrado"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Laboratorio:"), c);
        c.gridx = 1; panel.add(cboLaboratorio, c);
        c.gridx = 2; panel.add(new JLabel("Equipo dañado:"), c);
        c.gridx = 3; panel.add(cboEquipo, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Docente que reporta:"), c);
        c.gridx = 1; panel.add(cboDocente, c);
        c.gridx = 2; panel.add(new JLabel("Fecha y hora:"), c);
        c.gridx = 3; panel.add(txtFechaHora, c);

        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Tipo de falla:"), c);
        c.gridx = 1; panel.add(cboTipoFalla, c);
        c.gridx = 2; panel.add(new JLabel("Estado en que se encontró:"), c);
        c.gridx = 3; panel.add(cboEstadoEncontrado, c);

        c.gridx = 0; c.gridy = 3; panel.add(new JLabel("Descripción del problema:"), c);
        c.gridx = 1; c.gridwidth = 3; panel.add(new JScrollPane(txtDescripcion), c);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 1; panel.add(new JLabel("Observaciones:"), c);
        c.gridx = 1; c.gridwidth = 3; panel.add(txtObservaciones, c);

        return panel;
    }

    private JPanel panelFotos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Fotografías de evidencia (opcional, una o varias por reporte)"));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        botones.add(btnAgregarFoto);
        botones.add(btnQuitarFoto);
        panel.add(botones, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(lstFotos);
        scroll.setPreferredSize(new Dimension(700, 80));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------------- Carga de datos ----------------

    private void recargarCombos() {
        laboratorios.clear();
        cboLaboratorio.removeAllItems();
        try {
            for (Laboratorio l : laboratorioDao.listar()) {
                laboratorios.put(l.getId(), l);
                cboLaboratorio.addItem(new OpcionCombo(l.getId(), l.getCodigo() + " | " + l.getNombre()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los laboratorios: " + e.getMessage());
        }

        equipos.clear();
        try {
            for (Equipo e : equipoDao.listar()) {
                equipos.put(e.getId(), e);
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los equipos: " + e.getMessage());
        }

        docentes.clear();
        cboDocente.removeAllItems();
        try {
            for (Usuario u : usuarioDao.listarPorRol("Docente")) {
                docentes.put(u.getIdUsuario(), u);
                cboDocente.addItem(new OpcionCombo(u.getIdUsuario(),
                        u.getNombre() + " " + (u.getApellido() == null ? "" : u.getApellido())));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los docentes: " + e.getMessage());
        }

        if (cboLaboratorio.getItemCount() > 0) {
            cboLaboratorio.setSelectedIndex(0);
        }
        if (cboDocente.getItemCount() > 0) {
            cboDocente.setSelectedIndex(0);
        }
        txtFechaHora.setText(LocalDateTime.now().format(FORMATO));
        actualizarTabla();
    }

    private void cargarEquiposDelLaboratorio() {
        cboEquipo.removeAllItems();
        OpcionCombo lab = (OpcionCombo) cboLaboratorio.getSelectedItem();
        if (lab == null) {
            return;
        }
        for (Equipo e : equipos.values()) {
            if (e.getLaboratorioId() == lab.getId()) {
                cboEquipo.addItem(new OpcionCombo(e.getId(),
                        e.getCodigoInventario() + " | " + e.getTipo() + " " + e.getMarca()));
            }
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (ReporteDano r : servicio.listar()) {
                Laboratorio lab = laboratorios.get(r.getLaboratorioid());
                Equipo equipo = equipos.get(r.getEquipoid());
                Usuario docente = docentes.get(r.getDocenteid());
                int fotos;
                try {
                    fotos = servicio.fotosDeReporte(r.getId()).size();
                } catch (SQLException e) {
                    fotos = 0;
                }
                modeloTabla.addRow(new Object[]{
                        r.getId(),
                        r.getFechaHora() != null ? r.getFechaHora().format(FORMATO) : "-",
                        lab != null ? lab.getNombre() : "Lab " + r.getLaboratorioid(),
                        equipo != null ? equipo.getCodigoInventario() : "Equipo " + r.getEquipoid(),
                        docente != null ? docente.getNombre() : "Docente " + r.getDocenteid(),
                        r.getTipoFalla(),
                        r.getEstadoEncontrado(),
                        fotos
                });
            }
        } catch (SQLException e) {
            Ui.error(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void cargarSeleccionado() {
        int fila = tblReportes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (int) tblReportes.getValueAt(fila, 0);
        try {
            ReporteDano r = servicio.buscarPorId(id).orElse(null);
            if (r == null) {
                return;
            }
            editado = r;
            seleccionarPorId(cboLaboratorio, r.getLaboratorioid());
            cargarEquiposDelLaboratorio();
            seleccionarPorId(cboEquipo, r.getEquipoid());
            seleccionarPorId(cboDocente, r.getDocenteid());
            cboTipoFalla.setSelectedItem(r.getTipoFalla());
            txtFechaHora.setText(r.getFechaHora() != null ? r.getFechaHora().format(FORMATO) : "");
            cboEstadoEncontrado.setSelectedItem(r.getEstadoEncontrado());
            txtDescripcion.setText(r.getDescripcion());
            txtObservaciones.setText(r.getObservaciones());

            modeloFotos.clear();
            for (FotoEvidencia f : r.getFotos()) {
                modeloFotos.addElement(f.getRutaArchivo() + (f.getDescripcion() != null
                        && !f.getDescripcion().isEmpty() ? "  (" + f.getDescripcion() + ")" : ""));
            }
            btnActualizar.setEnabled(true);
        } catch (SQLException e) {
            Ui.error(this, "Error al cargar el reporte: " + e.getMessage());
        }
    }

    private void seleccionarPorId(JComboBox<OpcionCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    // ---------------- Fotos ----------------

    private void agregarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar fotografía de evidencia");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        if (!FotoEvidencia.validarFormato(archivo.getName())) {
            Ui.aviso(this, "Seleccione un archivo de imagen válido (.jpg, .jpeg, .png, .gif, .bmp, .webp).");
            return;
        }
        String descripcion = JOptionPane.showInputDialog(this,
                "Descripción de la fotografía (opcional):", "Descripción", JOptionPane.PLAIN_MESSAGE);
        if (descripcion == null) {
            descripcion = "";
        }
        FotoEvidencia foto = new FotoEvidencia();
        foto.setRutaArchivo(archivo.getAbsolutePath());
        foto.setDescripcion(descripcion.trim());
        if (editado != null) {
            editado.agregarFoto(foto);
        } else {
            fotosNuevas.add(foto);
        }
        modeloFotos.addElement(foto.getRutaArchivo()
                + (foto.getDescripcion().isEmpty() ? "" : "  (" + foto.getDescripcion() + ")"));
    }

    
    private final java.util.List<FotoEvidencia> fotosNuevas = new java.util.ArrayList<>();

    private void quitarFoto() {
        int indice = lstFotos.getSelectedIndex();
        if (indice < 0) {
            Ui.aviso(this, "Seleccione una fotografía de la lista.");
            return;
        }
        if (editado != null && indice < editado.getFotos().size()) {
            editado.getFotos().remove(indice);
        } else if (indice < fotosNuevas.size()) {
            fotosNuevas.remove(indice);
        }
        modeloFotos.remove(indice);
    }

    // ---------------- Acciones CRUD ----------------

    private void guardar() {
        ReporteDano r = crearObjetoDesdeFormulario(true);
        if (r == null) {
            return;
        }
        try {
            adjuntarFotos(r);
            servicio.registrar(r);
            fotosNuevas.clear();
            actualizarTabla();
            limpiar();
            Ui.info(this, "Reporte de daño guardado. El estado del equipo fue actualizado a DAÑADO.");
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (editado == null) {
            Ui.aviso(this, "Seleccione un reporte de la tabla para actualizarlo.");
            return;
        }
        ReporteDano r = crearObjetoDesdeFormulario(false);
        if (r == null) {
            return;
        }
        try {
            adjuntarFotos(r);
            servicio.actualizar(r);
            fotosNuevas.clear();
            actualizarTabla();
            limpiar();
            Ui.info(this, "Reporte actualizado correctamente.");
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    
    private void adjuntarFotos(ReporteDano r) {
        if (editado != null) {
            r.getFotos().addAll(editado.getFotos());
        }
        r.getFotos().addAll(fotosNuevas);
    }

    private void eliminar() {
        int fila = tblReportes.getSelectedRow();
        if (fila < 0) {
            Ui.aviso(this, "Seleccione un reporte de la tabla para eliminarlo.");
            return;
        }
        int id = (int) tblReportes.getValueAt(fila, 0);
        if (!Ui.confirmar(this, "¿Desea eliminar el reporte de daño seleccionado?"
                + " Las fotografías asociadas también se eliminarán.")) {
            return;
        }
        try {
            servicio.eliminar(id);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Reporte eliminado correctamente.");
        } catch (SQLException e) {
            Ui.error(this, "No se pudo eliminar: " + e.getMessage());
        }
    }

    private ReporteDano crearObjetoDesdeFormulario(boolean nuevo) {
        if (cboEquipo.getItemCount() == 0 || cboEquipo.getSelectedItem() == null) {
            Ui.aviso(this, "El laboratorio seleccionado no tiene equipos registrados.");
            return null;
        }
        if (cboDocente.getItemCount() == 0 || cboDocente.getSelectedItem() == null) {
            Ui.aviso(this, "No hay docentes registrados. Registre un usuario con rol Docente.");
            return null;
        }
        LocalDateTime fechaHora;
        try {
            fechaHora = LocalDateTime.parse(txtFechaHora.getText().trim(), FORMATO);
        } catch (Exception e) {
            Ui.aviso(this, "La fecha y hora debe tener el formato AAAA-MM-DD HH:MM.");
            return null;
        }
        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            Ui.aviso(this, "Describa el problema encontrado.");
            txtDescripcion.requestFocus();
            return null;
        }

        ReporteDano r = new ReporteDano();
        if (!nuevo && editado != null) {
            r.setId(editado.getId());
            r.setHorarioid(editado.getHorarioid());
        }
        r.setLaboratorioid(((OpcionCombo) cboLaboratorio.getSelectedItem()).getId());
        r.setEquipoid(((OpcionCombo) cboEquipo.getSelectedItem()).getId());
        r.setDocenteid(((OpcionCombo) cboDocente.getSelectedItem()).getId());
        r.setTipoFalla((String) cboTipoFalla.getSelectedItem());
        r.setDescripcion(descripcion);
        r.setFechaHora(fechaHora);
        r.setEstadoEncontrado((String) cboEstadoEncontrado.getSelectedItem());
        r.setObservaciones(txtObservaciones.getText().trim());
        return r;
    }

    private void limpiar() {
        editado = null;
        tblReportes.clearSelection();
        if (cboLaboratorio.getItemCount() > 0) {
            cboLaboratorio.setSelectedIndex(0);
        }
        cargarEquiposDelLaboratorio();
        if (cboDocente.getItemCount() > 0) {
            cboDocente.setSelectedIndex(0);
        }
        cboTipoFalla.setSelectedIndex(0);
        cboEstadoEncontrado.setSelectedIndex(0);
        txtDescripcion.setText("");
        txtObservaciones.setText("");
        txtFechaHora.setText(LocalDateTime.now().format(FORMATO));
        modeloFotos.clear();
        fotosNuevas.clear();
        btnActualizar.setEnabled(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmReporteDano().setVisible(true));
    }
}
