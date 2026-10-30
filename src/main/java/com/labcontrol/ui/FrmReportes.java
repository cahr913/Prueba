package com.labcontrol.UI;

import com.labcontrol.dao.CentroEducativoDao;
import com.labcontrol.dao.EquipoDao;
import com.labcontrol.dao.LaboratorioDao;
import com.labcontrol.dao.ReporteDao;
import com.labcontrol.dao.ResultadoReporte;
import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.CentroEducativo;
import com.labcontrol.modelo.Equipo;
import com.labcontrol.modelo.Laboratorio;
import com.labcontrol.modelo.Usuario;
import com.labcontrol.reportes.ReportePdf;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmReportes extends javax.swing.JFrame {

    private static final String[] NOMBRES_REPORTES = {
            "1. Inventario de equipos por laboratorio",
            "2. Equipos clasificados por estado",
            "3. Historial de mantenimiento de cada equipo",
            "4. Cantidad de mantenimientos realizados a todos los equipos",
            "5. Mantenimientos preventivos y correctivos",
            "6. Mantenimientos realizados por cada responsable",
            "7. Equipos con mayor cantidad de fallas o mantenimientos",
            "8. Piezas reparadas o reemplazadas",
            "9. Reportes de recepción registrados por los docentes",
            "10. Equipos dañados encontrados en cada laboratorio",
            "11. Programación de uso de los laboratorios"
    };

    private final ReporteDao reporteDao = new ReporteDao();
    private final LaboratorioDao laboratorioDao = new LaboratorioDao();
    private final EquipoDao equipoDao = new EquipoDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final CentroEducativoDao centroDao = new CentroEducativoDao();

    private final Map<Integer, Laboratorio> laboratorios = new HashMap<>();
    private final Map<Integer, Equipo> equipos = new HashMap<>();
    private final Map<Integer, Usuario> usuarios = new HashMap<>();

    private final JComboBox<String> cboReporte = new JComboBox<>(NOMBRES_REPORTES);
    private final JComboBox<OpcionCombo> cboLaboratorio = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboEquipo = new JComboBox<>();
    private final JComboBox<OpcionCombo> cboUsuario = new JComboBox<>();
    private final JComboBox<String> cboEstadoEquipo = new JComboBox<>();
    private final JComboBox<String> cboTipoMantenimiento = new JComboBox<>(new String[]{"- todos -", "Preventivo", "Correctivo"});
    private final JComboBox<String> cboAccionPieza = new JComboBox<>(new String[]{"- todos -", "REPARACION", "REEMPLAZO"});
    private final JTextField txtDesde = new JTextField(10);
    private final JTextField txtHasta = new JTextField(10);

    private final JButton btnGenerar = new JButton("Generar reporte");
    private final JButton btnExportar = new JButton("Exportar a PDF");

    private DefaultTableModel modeloTabla;
    private final JTable tblResultados = new JTable();
    private ResultadoReporte resultadoActual;

    public FrmReportes() {
        super("Consultas y Reportes");
        construir();
        recargarCombos();
    }

    private void construir() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("  Consultas y Reportes del Sistema");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(panelFiltros(), BorderLayout.NORTH);
        centro.add(panelTabla(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        setSize(1100, 620);
        setLocationRelativeTo(null);
    }

    private JPanel panelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reporte y filtros"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Reporte:"), c);
        c.gridx = 1; c.gridwidth = 3; panel.add(cboReporte, c);
        c.gridx = 4; c.gridwidth = 1; panel.add(btnGenerar, c);
        c.gridx = 5; panel.add(btnExportar, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Laboratorio:"), c);
        c.gridx = 1; panel.add(cboLaboratorio, c);
        c.gridx = 2; panel.add(new JLabel("Equipo:"), c);
        c.gridx = 3; panel.add(cboEquipo, c);
        c.gridx = 4; panel.add(new JLabel("Usuario:"), c);
        c.gridx = 5; panel.add(cboUsuario, c);

        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Estado equipo:"), c);
        c.gridx = 1; panel.add(cboEstadoEquipo, c);
        c.gridx = 2; panel.add(new JLabel("Tipo mantenimiento:"), c);
        c.gridx = 3; panel.add(cboTipoMantenimiento, c);
        c.gridx = 4; panel.add(new JLabel("Acción pieza:"), c);
        c.gridx = 5; panel.add(cboAccionPieza, c);

        c.gridx = 0; c.gridy = 3; panel.add(new JLabel("Fecha desde (AAAA-MM-DD):"), c);
        c.gridx = 1; panel.add(txtDesde, c);
        c.gridx = 2; panel.add(new JLabel("Fecha hasta:"), c);
        c.gridx = 3; panel.add(txtHasta, c);
        c.gridx = 4; c.gridwidth = 2;
        JLabel ayuda = new JLabel("Deje los filtros en 'Todos' para ver todo.");
        ayuda.setFont(ayuda.getFont().deriveFont(Font.ITALIC, 10f));
        panel.add(ayuda, c);

        btnGenerar.addActionListener(e -> generar());
        btnExportar.addActionListener(e -> exportarPdf());
        return panel;
    }

    private JPanel panelTabla() {
        modeloTabla = new DefaultTableModel();
        tblResultados.setModel(modeloTabla);
        tblResultados.setRowHeight(22);
        tblResultados.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tblResultados);
        scroll.setPreferredSize(new Dimension(1050, 420));
        JPanel p = new JPanel(new BorderLayout());
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void recargarCombos() {
        laboratorios.clear();
        cboLaboratorio.removeAllItems();
        cboLaboratorio.addItem(new OpcionCombo(0, "- todos -"));
        try {
            for (Laboratorio l : laboratorioDao.listar()) {
                laboratorios.put(l.getId(), l);
                cboLaboratorio.addItem(new OpcionCombo(l.getId(), l.getCodigo() + " | " + l.getNombre()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los laboratorios: " + e.getMessage());
        }

        equipos.clear();
        cboEquipo.removeAllItems();
        cboEquipo.addItem(new OpcionCombo(0, "- todos -"));
        try {
            for (Equipo e : equipoDao.listar()) {
                equipos.put(e.getId(), e);
                cboEquipo.addItem(new OpcionCombo(e.getId(), e.getCodigoInventario() + " | " + e.getTipo()));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los equipos: " + e.getMessage());
        }

        usuarios.clear();
        cboUsuario.removeAllItems();
        cboUsuario.addItem(new OpcionCombo(0, "- todos -"));
        try {
            for (Usuario u : usuarioDao.listar()) {
                usuarios.put(u.getIdUsuario(), u);
                cboUsuario.addItem(new OpcionCombo(u.getIdUsuario(),
                        u.getNombre() + " " + (u.getApellido() == null ? "" : u.getApellido())
                                + " (" + u.getRol() + ")"));
            }
        } catch (SQLException e) {
            Ui.error(this, "No se pudieron cargar los usuarios: " + e.getMessage());
        }

        DefaultComboBoxModel<String> modeloEstado = new DefaultComboBoxModel<>();
        modeloEstado.addElement("- todos -");
        for (String e : Equipo.ESTADOS) {
            modeloEstado.addElement(e);
        }
        cboEstadoEquipo.setModel(modeloEstado);
    }

    private void generar() {
        ReporteDao.Filtros f = construirFiltros();
        if (f == null) {
            return;
        }
        int indice = cboReporte.getSelectedIndex();
        try {
            switch (indice) {
                case 0: resultadoActual = reporteDao.inventarioPorLaboratorio(f); break;
                case 1: resultadoActual = reporteDao.equiposPorEstado(f); break;
                case 2: resultadoActual = reporteDao.historialMantenimiento(f); break;
                case 3: resultadoActual = reporteDao.cantidadMantenimientos(f); break;
                case 4: resultadoActual = reporteDao.mantenimientosPorTipo(f); break;
                case 5: resultadoActual = reporteDao.mantenimientosPorResponsable(f); break;
                case 6: resultadoActual = reporteDao.equiposConMasFallas(f); break;
                case 7: resultadoActual = reporteDao.piezasReparadas(f); break;
                case 8: resultadoActual = reporteDao.recepcionesPorDocente(f); break;
                case 9: resultadoActual = reporteDao.equiposDanadosPorLaboratorio(f); break;
                default: resultadoActual = reporteDao.programacionLaboratorios(f); break;
            }
            mostrarResultado(resultadoActual);
        } catch (SQLException e) {
            Ui.error(this, "Error al generar el reporte: " + e.getMessage());
        }
    }

    private ReporteDao.Filtros construirFiltros() {
        ReporteDao.Filtros f = new ReporteDao.Filtros();
        f.laboratorioId = ((OpcionCombo) cboLaboratorio.getSelectedItem()).getId();
        f.equipoId = ((OpcionCombo) cboEquipo.getSelectedItem()).getId();
        f.docenteId = ((OpcionCombo) cboUsuario.getSelectedItem()).getId();
        f.responsableId = ((OpcionCombo) cboUsuario.getSelectedItem()).getId();
        String estado = (String) cboEstadoEquipo.getSelectedItem();
        f.estadoEquipo = "- todos -".equals(estado) ? null : estado;
        String tipo = (String) cboTipoMantenimiento.getSelectedItem();
        f.tipoMantenimiento = "- todos -".equals(tipo) ? null : tipo;
        String accion = (String) cboAccionPieza.getSelectedItem();
        f.accionPieza = "- todos -".equals(accion) ? null : accion;

        String desde = txtDesde.getText().trim();
        String hasta = txtHasta.getText().trim();
        if (!desde.isEmpty()) {
            if (!Ui.fechaValida(desde)) {
                Ui.aviso(this, "La fecha 'desde' debe tener el formato AAAA-MM-DD.");
                return null;
            }
            f.desde = LocalDate.parse(desde);
        }
        if (!hasta.isEmpty()) {
            if (!Ui.fechaValida(hasta)) {
                Ui.aviso(this, "La fecha 'hasta' debe tener el formato AAAA-MM-DD.");
                return null;
            }
            f.hasta = LocalDate.parse(hasta);
        }
        return f;
    }

    private void mostrarResultado(ResultadoReporte r) {
        if (r == null) {
            return;
        }
        modeloTabla.setColumnIdentifiers(r.getEncabezados());
        modeloTabla.setRowCount(0);
        for (Object[] fila : r.getFilas()) {
            modeloTabla.addRow(fila);
        }
        if (r.estaVacio()) {
            Ui.aviso(this, "El reporte no devolvió resultados con los filtros indicados.");
        }
    }

    private void exportarPdf() {
        if (resultadoActual == null || resultadoActual.estaVacio()) {
            Ui.aviso(this, "Genere un reporte con resultados antes de exportarlo.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte en PDF");
        chooser.setSelectedFile(new File("Reporte_" + (cboReporte.getSelectedIndex() + 1) + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }
        try {
            List<CentroEducativo> centros = centroDao.listar();
            CentroEducativo centro = centros.isEmpty() ? null : centros.get(0);
            ReportePdf.exportar(archivo, resultadoActual, centro);
            Ui.info(this, "Reporte exportado correctamente a:\n" + archivo.getAbsolutePath());
        } catch (Exception e) {
            Ui.error(this, "No se pudo exportar el PDF: " + e.getMessage());
        }
    }
}
