package com.labcontrol.UI;

import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Recepcion;
import com.labcontrol.modelo.Usuario;
import com.labcontrol.servicio.RecepcionServicio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmRecepcion extends javax.swing.JFrame {

    private static final String[] CALIFICATIVOS = {"Excelente", "Bueno", "Regular", "Malo"};
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RecepcionServicio servicio = new RecepcionServicio();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final EquipoDao equipoDao = new EquipoDao();

    private final Map<Integer, Laboratorio> laboratorios = new HashMap<>();
    private final Map<Integer, Usuario> docentes = new HashMap<>();

    private Recepcion editado;

    private final JComboBox<OpcionCombo> cboLaboratorio = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboDocente = new JComboBox<>();
    private final JTextField txtFechaHora = new JTextField(16);
    private final JComboBox<String> cboEstadoGeneral = new JComboBox<>(CALIFICATIVOS);
    private final JComboBox<String> cboLimpieza = new JComboBox<>(CALIFICATIVOS);
    private final JComboBox<String> cboMobiliario = new JComboBox<>(CALIFICATIVOS);
    private final JCheckBox chkEnergia = new JCheckBox("Energía eléctrica funcionando", true);
    private final JCheckBox chkRed = new JCheckBox("Red funcionando", true);
    private final JComboBox<String> cboEstadoEquipos = new JComboBox<>(CALIFICATIVOS);
    private final JTextField txtObservaciones = new JTextField(22);

    private final DefaultTableModel modeloEquipos = new DefaultTableModel(
            new Object[]{"¿Dañado?", "Código", "Tipo", "Marca", "Estado actual"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == 0;
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 0 ? Boolean.class : String.class;
        }
    };
    private final javax.swing.JTable tblEquipos = new javax.swing.JTable(modeloEquipos);

    private final JButton btnGuardar = new JButton("Guardar nueva recepción");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private DefaultTableModel modeloTabla;
    private final javax.swing.JTable tblRecepciones = new javax.swing.JTable();

    public FrmRecepcion() {
        super("Recepción del Laboratorio");
        construir();
        recargarCombos();
        btnActualizar.setEnabled(false);
    }

    // ---------------- Construccion ----------------

    private void construir() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("  Recepción del Laboratorio");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(panelFormulario(), BorderLayout.NORTH);
        centro.add(panelEquipos(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        sur.add(botones, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Fecha y hora", "Laboratorio", "Docente", "Estado general",
                "Limpieza", "Mobiliario", "Energía", "Red", "Equipos dañados"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblRecepciones.setModel(modeloTabla);
        tblRecepciones.setRowHeight(24);
        tblRecepciones.getTableHeader().setReorderingAllowed(false);
        tblRecepciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionado();
            }
        });
        JScrollPane scroll = new JScrollPane(tblRecepciones);
        scroll.setPreferredSize(new Dimension(950, 170));
        sur.add(scroll, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        cboLaboratorio.addActionListener(e -> cargarEquiposDelLaboratorio());

        setSize(1000, 760);
        setLocationRelativeTo(null);
    }

    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Estado en que el docente recibe el laboratorio"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Laboratorio:"), c);
        c.gridx = 1; panel.add(cboLaboratorio, c);
        c.gridx = 2; panel.add(new JLabel("Docente que recibe:"), c);
        c.gridx = 3; panel.add(cboDocente, c);
        c.gridx = 4; panel.add(new JLabel("Fecha y hora:"), c);
        c.gridx = 5; panel.add(txtFechaHora, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Estado general:"), c);
        c.gridx = 1; panel.add(cboEstadoGeneral, c);
        c.gridx = 2; panel.add(new JLabel("Limpieza:"), c);
        c.gridx = 3; panel.add(cboLimpieza, c);
        c.gridx = 4; panel.add(new JLabel("Mobiliario:"), c);
        c.gridx = 5; panel.add(cboMobiliario, c);

        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Estado de los equipos:"), c);
        c.gridx = 1; panel.add(cboEstadoEquipos, c);
        c.gridx = 2; c.gridwidth = 2; panel.add(chkEnergia, c);
        c.gridx = 4; c.gridwidth = 2; panel.add(chkRed, c);

        c.gridx = 0; c.gridy = 3; panel.add(new JLabel("Observaciones:"), c);
        c.gridx = 1; c.gridwidth = 5; panel.add(txtObservaciones, c);

        return panel;
    }

    private JPanel panelEquipos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Equipos del laboratorio: marque los que encuentre dañados"));
        tblEquipos.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tblEquipos);
        scroll.setPreferredSize(new Dimension(700, 130));
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
                cboLaboratorio.addItem(new OpcionCombo(l.getId(),
                        l.getCodigo() + " | " + l.getNombre()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los laboratorios: " + e.getMessage());
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
        modeloEquipos.setRowCount(0);
        OpcionCombo seleccionado = (OpcionCombo) cboLaboratorio.getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        try {
            for (Equipo e : equipoDao.listar()) {
                if (e.getLaboratorioId() == seleccionado.getId()) {
                    modeloEquipos.addRow(new Object[]{false, e.getCodigoInventario(),
                            e.getTipo(), e.getMarca(), e.getEstado()});
                }
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los equipos: " + e.getMessage());
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Recepcion r : servicio.listar()) {
                Laboratorio lab = laboratorios.get(r.getLaboratorioId());
                Usuario docente = docentes.get(r.getDocenteId());
                modeloTabla.addRow(new Object[]{
                        r.getId(),
                        r.getFechaHora() != null ? r.getFechaHora().format(FORMATO) : "-",
                        lab != null ? lab.getNombre() : "Lab " + r.getLaboratorioId(),
                        docente != null ? docente.getNombre() : "Docente " + r.getDocenteId(),
                        r.getEstadoGeneral(),
                        r.getLimpieza(),
                        r.getEstadoMobiliario(),
                        r.isEnergiaElectrica() ? "Sí" : "No",
                        r.isRed() ? "Sí" : "No",
                        r.getEquiposDanadosIds().size()
                });
            }
        } catch (SQLException e) {
            Ui.error(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void cargarSeleccionado() {
        int fila = tblRecepciones.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (int) tblRecepciones.getValueAt(fila, 0);
        try {
            Recepcion r = servicio.buscarPorId(id).orElse(null);
            if (r == null) {
                return;
            }
            editado = r;
            seleccionarPorId(cboLaboratorio, r.getLaboratorioId());
            seleccionarPorId(cboDocente, r.getDocenteId());
            txtFechaHora.setText(r.getFechaHora() != null ? r.getFechaHora().format(FORMATO) : "");
            cboEstadoGeneral.setSelectedItem(r.getEstadoGeneral());
            cboLimpieza.setSelectedItem(r.getLimpieza());
            cboMobiliario.setSelectedItem(r.getEstadoMobiliario());
            cboEstadoEquipos.setSelectedItem(r.getEstadoEquipos());
            chkEnergia.setSelected(r.isEnergiaElectrica());
            chkRed.setSelected(r.isRed());
            txtObservaciones.setText(r.getObservaciones());

            // Marcar los equipos dañados ya registrados en esa recepcion.
            cargarEquiposDelLaboratorio();
            for (int i = 0; i < modeloEquipos.getRowCount(); i++) {
                String codigo = String.valueOf(modeloEquipos.getValueAt(i, 1));
                // Buscar el id del equipo por codigo usando el DAO.
                int equipoId = buscarIdEquipoPorCodigo(codigo);
                if (equipoId > 0 && r.getEquiposDanadosIds().contains(equipoId)) {
                    modeloEquipos.setValueAt(true, i, 0);
                }
            }
            btnActualizar.setEnabled(true);
        } catch (SQLException e) {
            Ui.error(this, "Error al cargar la recepción: " + e.getMessage());
        }
    }

    private int buscarIdEquipoPorCodigo(String codigo) {
        try {
            for (Equipo e : equipoDao.listar()) {
                if (codigo.equals(e.getCodigoInventario())) {
                    return e.getId();
                }
            }
        } catch (SQLException ignorado) {
            // Si falla la busqueda, simplemente no se marca la casilla.
        }
        return 0;
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

    // ---------------- Acciones CRUD ----------------

    private void guardar() {
        Recepcion r = crearObjetoDesdeFormulario(true);
        if (r == null) {
            return;
        }
        try {
            servicio.registrar(r);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Recepción registrada correctamente"
                    + (r.getEquiposDanadosIds().isEmpty() ? "."
                            : " con " + r.getEquiposDanadosIds().size() + " equipo(s) dañado(s)."));
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (editado == null) {
            Ui.aviso(this, "Seleccione una recepción de la tabla para actualizarla.");
            return;
        }
        Recepcion r = crearObjetoDesdeFormulario(false);
        if (r == null) {
            return;
        }
        try {
            servicio.actualizar(r);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Recepción actualizada correctamente.");
        } catch (SQLException e) {
            Ui.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        int fila = tblRecepciones.getSelectedRow();
        if (fila < 0) {
            Ui.aviso(this, "Seleccione una recepción de la tabla para eliminarla.");
            return;
        }
        int id = (int) tblRecepciones.getValueAt(fila, 0);
        if (!Ui.confirmar(this, "¿Desea eliminar la recepción seleccionada?")) {
            return;
        }
        try {
            servicio.eliminar(id);
            actualizarTabla();
            limpiar();
            Ui.info(this, "Recepción eliminada correctamente.");
        } catch (SQLException e) {
            Ui.error(this, "No se pudo eliminar: " + e.getMessage());
        }
    }

    private Recepcion crearObjetoDesdeFormulario(boolean nuevo) {
        if (cboLaboratorio.getItemCount() == 0 || cboLaboratorio.getSelectedItem() == null) {
            Ui.aviso(this, "No hay laboratorios registrados.");
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
            Ui.aviso(this, "La fecha y hora debe tener el formato AAAA-MM-DD HH:MM (ejemplo: "
                    + LocalDateTime.now().format(FORMATO) + ").");
            return null;
        }

        Recepcion r = new Recepcion();
        if (!nuevo && editado != null) {
            r.setId(editado.getId());
            r.setHorarioId(editado.getHorarioId());
        }
        r.setLaboratorioId(((OpcionCombo) cboLaboratorio.getSelectedItem()).getId());
        r.setDocenteId(((OpcionCombo) cboDocente.getSelectedItem()).getId());
        r.setFechaHora(fechaHora);
        r.setEstadoGeneral((String) cboEstadoGeneral.getSelectedItem());
        r.setLimpieza((String) cboLimpieza.getSelectedItem());
        r.setEstadoMobiliario((String) cboMobiliario.getSelectedItem());
        r.setEnergiaElectrica(chkEnergia.isSelected());
        r.setRed(chkRed.isSelected());
        r.setEstadoEquipos((String) cboEstadoEquipos.getSelectedItem());
        r.setObservaciones(txtObservaciones.getText().trim());

        // Equipos marcados como dañados en la tabla.
        List<Integer> danados = new ArrayList<>();
        for (int i = 0; i < modeloEquipos.getRowCount(); i++) {
            boolean marcado = Boolean.TRUE.equals(modeloEquipos.getValueAt(i, 0));
            if (marcado) {
                int equipoId = buscarIdEquipoPorCodigo(String.valueOf(modeloEquipos.getValueAt(i, 1)));
                if (equipoId > 0) {
                    danados.add(equipoId);
                }
            }
        }
        r.setEquiposDanadosIds(danados);
        return r;
    }

    private void limpiar() {
        editado = null;
        tblRecepciones.clearSelection();
        if (cboLaboratorio.getItemCount() > 0) {
            cboLaboratorio.setSelectedIndex(0);
        }
        if (cboDocente.getItemCount() > 0) {
            cboDocente.setSelectedIndex(0);
        }
        cboEstadoGeneral.setSelectedIndex(1);
        cboLimpieza.setSelectedIndex(1);
        cboMobiliario.setSelectedIndex(1);
        cboEstadoEquipos.setSelectedIndex(1);
        chkEnergia.setSelected(true);
        chkRed.setSelected(true);
        txtObservaciones.setText("");
        txtFechaHora.setText(LocalDateTime.now().format(FORMATO));
        cargarEquiposDelLaboratorio();
        btnActualizar.setEnabled(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmRecepcion().setVisible(true));
    }
}
