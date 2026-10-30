package com.labcontrol.UI;

import com.labcontrol.dao.HorarioDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.Horario;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Usuario;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

public class PnlHorarios extends javax.swing.JPanel implements Refrescable {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final HorarioDao dao = new HorarioDao();
    private final LaboratorioDao labDao = new LaboratorioDao();
    private final UsuarioDao userDao = new UsuarioDao();
    private final List<Horario> visibles = new ArrayList<>();
    private final List<Laboratorio> laboratorios = new ArrayList<>();
    private final List<Usuario> docentes = new ArrayList<>();
    private DefaultTableModel modelo;
    private Horario editado;
    private boolean cargando = true;

    public PnlHorarios() {
        initComponents();
        iniciar();
    }

    private void iniciar() {
        Ui.estiloTitulo(lblTitulo);
        Ui.configurarTabla(tblHorarios, 60, 200, 60, 60, 60, 180, 120);
        modelo = (DefaultTableModel) tblHorarios.getModel();
        refrescar();
    }

    @Override
    public void refrescar() {
        cargando = true;
        try {
            cargarCombos();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
        cargando = false;
        cargarTabla();
        cancelarEdicion();
    }

    private void cargarCombos() throws SQLException {
        laboratorios.clear();
        laboratorios.addAll(labDao.listar());
        docentes.clear();
        docentes.addAll(userDao.listarPorRol("Docente"));

        cboFiltroLaboratorio.removeAllItems();
        cboLaboratorio.removeAllItems();
        cboFiltroLaboratorio.addItem("- todos -");
        cboLaboratorio.addItem("<Seleccione>");
        for (Laboratorio l : laboratorios) {
            cboFiltroLaboratorio.addItem(l);
            cboLaboratorio.addItem(l);
        }

        cboDocente.removeAllItems();
        cboDocente.addItem("<Seleccione>");
        for (Usuario d : docentes) {
            cboDocente.addItem(d);
        }

        cboDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        }));
    }

    private void cargarTabla() {
        Object laboratorioFiltro = cboFiltroLaboratorio.getSelectedItem();
        Object diaFiltro = cboFiltroDia.getSelectedItem();
        visibles.clear();
        try {
            for (Horario horario : dao.listar()) {
                boolean coincideLaboratorio = !(laboratorioFiltro instanceof Laboratorio)
                        || horario.getLaboratorioId() == ((Laboratorio) laboratorioFiltro).getId();
                boolean coincideDia = "- todos -".equals(diaFiltro) || (horario.getDia() != null && horario.getDia().equalsIgnoreCase(String.valueOf(diaFiltro)));
                if (coincideLaboratorio && coincideDia) {
                    visibles.add(horario);
                }
            }
            visibles.sort(Comparator
                    .comparing((Horario h) -> h.getLaboratorioId())
                    .thenComparingInt(h -> Arrays.asList("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo").indexOf(h.getDia()))
                    .thenComparing(Horario::getHoraInicio));
            modelo.setRowCount(0);
            for (Horario horario : visibles) {
                String labNombre = "";
                for (Laboratorio l : laboratorios) {
                    if (l.getId() == horario.getLaboratorioId()) {
                        labNombre = l.getNombre();
                        break;
                    }
                }
                String docenteNombre = "";
                for (Usuario u : docentes) {
                    if (u.getIdUsuario() == horario.getDocenteId()) {
                        docenteNombre = u.getUsuario();
                        break;
                    }
                }
                modelo.addRow(new Object[]{
                    horario.getId(), labNombre, docenteNombre, horario.getDia(),
                    horario.getHoraInicio().format(FORMATO_HORA),
                    horario.getHoraFin().format(FORMATO_HORA),
                    horario.getCurso()
                });
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar horarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        editado = null;
        if (cboLaboratorio.getItemCount() > 0) {
            cboLaboratorio.setSelectedIndex(0);
        }
        cboDocente.setSelectedIndex(0);
        cboDia.setSelectedIndex(0);
        txtInicio.setText("");
        txtFin.setText("");
        txtActividad.setText("");
    }

    private void cancelarEdicion() {
        limpiarCampos();
        Ui.habilitar(pnlFormulario, false);
    }

    private void nuevo() {
        if (laboratorios.isEmpty()) {
            Ui.aviso(this, "Registre al menos un laboratorio antes de crear horarios.");
            return;
        }
        if (docentes.isEmpty()) {
            Ui.aviso(this, "Registre al menos un docente antes de crear horarios.");
            return;
        }
        tblHorarios.clearSelection();
        limpiarCampos();
        Ui.habilitar(pnlFormulario, true);
        txtInicio.requestFocusInWindow();
    }

    private void editar() {
        int fila = Ui.filaSeleccionada(this, tblHorarios);
        if (fila < 0) {
            return;
        }
        editado = visibles.get(fila);
        for (int i = 0; i < cboLaboratorio.getItemCount(); i++) {
            Object item = cboLaboratorio.getItemAt(i);
            if (item instanceof Laboratorio
                    && ((Laboratorio) item).getId() == editado.getLaboratorioId()) {
                cboLaboratorio.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cboDocente.getItemCount(); i++) {
            Object item = cboDocente.getItemAt(i);
            if (item instanceof Usuario && ((Usuario) item).getIdUsuario() == editado.getDocenteId()) {
                cboDocente.setSelectedIndex(i);
                break;
            }
        }
        cboDia.setSelectedItem(editado.getDia());
        txtInicio.setText(editado.getHoraInicio().format(FORMATO_HORA));
        txtFin.setText(editado.getHoraFin().format(FORMATO_HORA));
        txtActividad.setText(editado.getCurso());
        Ui.habilitar(pnlFormulario, true);
    }

    private void eliminar() {
        int fila = Ui.filaSeleccionada(this, tblHorarios);
        if (fila < 0) {
            return;
        }
        Horario horario = visibles.get(fila);
        String descripcion = horario.getCurso() + " (" + horario.getDia() + " "
                + horario.getHoraInicio().format(FORMATO_HORA) + " - "
                + horario.getHoraFin().format(FORMATO_HORA) + ")";
        if (!Ui.confirmar(this, "¿Desea eliminar el horario «" + descripcion + "»?")) {
            return;
        }
        try {
            dao.eliminar(horario.getId());
            if (horario == editado) {
                cancelarEdicion();
            }
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "No se pudo eliminar el horario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardar() {
        Laboratorio laboratorio = (Laboratorio) cboLaboratorio.getSelectedItem();
        Usuario docente = (Usuario) cboDocente.getSelectedItem();
        String dia = (String) cboDia.getSelectedItem();
        String actividad = txtActividad.getText().trim();

        if (laboratorio == null || laboratorio.getId() <= 0) {
            Ui.aviso(this, "Seleccione un laboratorio.");
            return;
        }
        if (docente == null || docente.getIdUsuario() <= 0) {
            Ui.aviso(this, "Seleccione un docente.");
            return;
        }
        LocalTime inicio = leerHora(txtInicio.getText());
        if (inicio == null) {
            Ui.aviso(this, "La hora de inicio no es válida. Use el formato HH:mm, por ejemplo 08:30.");
            txtInicio.requestFocusInWindow();
            return;
        }
        LocalTime fin = leerHora(txtFin.getText());
        if (fin == null) {
            Ui.aviso(this, "La hora de fin no es válida. Use el formato HH:mm, por ejemplo 10:00.");
            txtFin.requestFocusInWindow();
            return;
        }
        if (!inicio.isBefore(fin)) {
            Ui.aviso(this, "La hora de inicio debe ser anterior a la hora de fin.");
            txtInicio.requestFocusInWindow();
            return;
        }
        if (actividad.isEmpty()) {
            Ui.aviso(this, "Ingrese la actividad del horario.");
            txtActividad.requestFocusInWindow();
            return;
        }

        Horario h = new Horario();
        h.setLaboratorioId(laboratorio.getId());
        h.setDocenteId(docente.getIdUsuario());
        h.setCurso(actividad);
        h.setDia(dia);
        LocalDate fechaDia = LocalDate.now();
        while (!fechaDia.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).equalsIgnoreCase(dia)) {
            fechaDia = fechaDia.plusDays(1);
        }
        h.setFecha(fechaDia);
        h.setHoraInicio(inicio);
        h.setHoraFin(fin);
        h.setObservaciones("");

        try {
            if (editado == null) {
                if (dao.existeSolapamiento(laboratorio.getId(), dia, inicio, fin, -1)) {
                    Ui.aviso(this, "El horario se superpone con otro registrado para el mismo laboratorio y día.");
                    return;
                }
                dao.insertar(h);
            } else {
                if (dao.existeSolapamiento(laboratorio.getId(), dia, inicio, fin, editado.getId())) {
                    Ui.aviso(this, "El horario se superpone con otro registrado para el mismo laboratorio y día.");
                    return;
                }
                h.setId(editado.getId());
                dao.actualizar(h);
            }
            cancelarEdicion();
            cboFiltroLaboratorio.setSelectedIndex(0);
            cboFiltroDia.setSelectedIndex(0);
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static LocalTime leerHora(String texto) {
        String valor = texto.trim();
        if (!valor.matches("\\d{1,2}:\\d{2}")) {
            return null;
        }
        String[] partes = valor.split(":");
        try {
            return LocalTime.of(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
        } catch (DateTimeException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblFiltroLaboratorio = new javax.swing.JLabel();
        cboFiltroLaboratorio = new javax.swing.JComboBox<>();
        lblFiltroDia = new javax.swing.JLabel();
        cboFiltroDia = new javax.swing.JComboBox<>();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        scrHorarios = new javax.swing.JScrollPane();
        tblHorarios = new javax.swing.JTable();
        pnlFormulario = new javax.swing.JPanel();
        lblLaboratorio = new javax.swing.JLabel();
        cboLaboratorio = new javax.swing.JComboBox<>();
        lblDocente = new javax.swing.JLabel();
        cboDocente = new javax.swing.JComboBox<>();
        lblDia = new javax.swing.JLabel();
        cboDia = new javax.swing.JComboBox<>();
        lblInicio = new javax.swing.JLabel();
        txtInicio = new javax.swing.JTextField();
        lblFin = new javax.swing.JLabel();
        txtFin = new javax.swing.JTextField();
        lblActividad = new javax.swing.JLabel();
        txtActividad = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        lblTitulo.setText("Horarios");

        lblFiltroLaboratorio.setText("Laboratorio:");

        cboFiltroLaboratorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroLaboratorioActionPerformed(evt);
            }
        });

        lblFiltroDia.setText("Día:");

        cboFiltroDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- todos -", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" }));
        cboFiltroDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroDiaActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo horario");
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

        tblHorarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Laboratorio", "Docente", "Día", "Inicio", "Fin", "Actividad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return canEdit [columnIndex];

            }
        });


        scrHorarios.setViewportView(tblHorarios);

        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Horario"));
        lblLaboratorio.setText("Laboratorio");



        lblDocente.setText("Docente");



        lblDia.setText("Día");



        lblInicio.setText("Hora de inicio (HH:mm)");



        lblFin.setText("Hora de fin (HH:mm)");



        lblActividad.setText("Actividad");



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
                        .addComponent(lblLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLaboratorio, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboDocente, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboDia, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFin, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtActividad, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
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
                        .addComponent(lblLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lblFiltroLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboFiltroLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)
                    .addComponent(lblFiltroDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboFiltroDia, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrHorarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(lblFiltroLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboFiltroLaboratorio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFiltroDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboFiltroDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrHorarios, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
    }

    private void cboFiltroLaboratorioActionPerformed(java.awt.event.ActionEvent evt) {
        if (!cargando) {
            cargarTabla();
        }
    }

    private void cboFiltroDiaActionPerformed(java.awt.event.ActionEvent evt) {
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

    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblFiltroLaboratorio;
    private javax.swing.JComboBox<Object> cboFiltroLaboratorio;
    private javax.swing.JLabel lblFiltroDia;
    private javax.swing.JComboBox<String> cboFiltroDia;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JScrollPane scrHorarios;
    private javax.swing.JTable tblHorarios;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JLabel lblLaboratorio;
    private javax.swing.JComboBox<Object> cboLaboratorio;
    private javax.swing.JLabel lblDocente;
    private javax.swing.JComboBox<Object> cboDocente;
    private javax.swing.JLabel lblDia;
    private javax.swing.JComboBox<String> cboDia;
    private javax.swing.JLabel lblInicio;
    private javax.swing.JTextField txtInicio;
    private javax.swing.JLabel lblFin;
    private javax.swing.JTextField txtFin;
    private javax.swing.JLabel lblActividad;
    private javax.swing.JTextField txtActividad;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnCancelar;
}
