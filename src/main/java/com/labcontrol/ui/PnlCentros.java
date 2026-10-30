package com.labcontrol.UI;

import com.labcontrol.dao.CentroEducativoDao;
import com.labcontrol.modelo.CentroEducativo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PnlCentros extends javax.swing.JPanel implements Refrescable {

    private final CentroEducativoDao dao = new CentroEducativoDao();
    private final List<CentroEducativo> visibles = new ArrayList<>();
    private DefaultTableModel modelo;
    private CentroEducativo editado;
    private String ultimaBusqueda = "";

    private final JLabel lblTitulo = new JLabel("Centros Educativos");
    private final JLabel lblBuscar = new JLabel("Buscar centro (nombre o código):");
    private final JTextField txtBusqueda = new JTextField(24);
    private final JButton btnNuevo = new JButton("Nuevo centro");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JTable tblCentros = new JTable();
    private final JScrollPane scrCentros = new JScrollPane(tblCentros);

    private final JPanel pnlFormulario = new JPanel(new GridBagLayout());
    private final JTextField txtCodigo = new JTextField(14);
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDireccion = new JTextField(22);
    private final JTextField txtTelefono = new JTextField(16);
    private final JTextField txtCorreo = new JTextField(22);
    private final JTextField txtDirector = new JTextField(22);
    private final JLabel lblLogo = new JLabel("(sin logotipo)");
    private final JButton btnLogo = new JButton("Seleccionar logotipo...");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    public PnlCentros() {
        construir();
        iniciar();
    }

    private void construir() {
        setLayout(new BorderLayout(8, 8));
        Ui.estiloTitulo(lblTitulo);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(lblBuscar);
        top.add(txtBusqueda);
        top.add(btnNuevo);
        top.add(btnEditar);
        top.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout(4, 4));
        norte.add(lblTitulo, BorderLayout.NORTH);
        norte.add(top, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        tblCentros.setModel(new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Dirección", "Teléfono", "Correo", "Director"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        tblCentros.setRowHeight(24);
        tblCentros.getTableHeader().setReorderingAllowed(false);
        Ui.configurarTabla(tblCentros, 100, 230, 230, 130, 200, 200);
        scrCentros.setPreferredSize(new Dimension(980, 260));
        add(scrCentros, BorderLayout.CENTER);

        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Centro Educativo"));
        add(pnlFormulario, BorderLayout.SOUTH);
    }

    private void iniciar() {
        modelo = (DefaultTableModel) tblCentros.getModel();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(pnlFormulario, c, 0, fila, "Código", txtCodigo);
        agregarCampo(pnlFormulario, c, 2, fila, "Nombre", txtNombre);
        fila++;
        agregarCampo(pnlFormulario, c, 0, fila, "Dirección", txtDireccion);
        agregarCampo(pnlFormulario, c, 2, fila, "Teléfono", txtTelefono);
        fila++;
        agregarCampo(pnlFormulario, c, 0, fila, "Correo", txtCorreo);
        agregarCampo(pnlFormulario, c, 2, fila, "Director(a)", txtDirector);
        fila++;
        c.gridx = 0; c.gridy = fila; c.gridwidth = 1;
        pnlFormulario.add(new JLabel("Logotipo"), c);
        c.gridx = 1; c.gridwidth = 3;
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        logoPanel.add(btnLogo);
        logoPanel.add(lblLogo);
        pnlFormulario.add(logoPanel, c);
        c.gridx = 4; c.gridwidth = 1;
        pnlFormulario.add(btnGuardar, c);
        c.gridx = 5; c.gridwidth = 1;
        pnlFormulario.add(btnCancelar, c);

        txtBusqueda.addCaretListener(e -> {
            if (modelo != null && !txtBusqueda.getText().equals(ultimaBusqueda)) {
                cargarTabla();
            }
        });
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelarEdicion());
        btnLogo.addActionListener(e -> seleccionarLogo());
        refrescar();
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int columna, int fila,
                              String etiqueta, JTextField campo) {
        c.gridx = columna;
        c.gridy = fila;
        c.gridwidth = 1;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = columna + 1;
        c.gridwidth = 1;
        panel.add(campo, c);
    }

    @Override
    public void refrescar() {
        cargarTabla();
        cancelarEdicion();
    }

    private void cargarTabla() {
        ultimaBusqueda = txtBusqueda.getText();
        String texto = ultimaBusqueda.trim().toLowerCase();
        visibles.clear();
        modelo.setRowCount(0);
        try {
            for (CentroEducativo centro : dao.listar()) {
                boolean coincide = texto.isEmpty()
                        || centro.getNombre().toLowerCase().contains(texto)
                        || centro.getCodigo().toLowerCase().contains(texto);
                if (coincide) {
                    visibles.add(centro);
                    modelo.addRow(new Object[]{centro.getCodigo(), centro.getNombre(), centro.getDireccion(),
                            centro.getTelefono(), centro.getCorreo(), centro.getDirector()});
                }
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar centros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        editado = null;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDirector.setText("");
        lblLogo.setText("(sin logotipo)");
    }

    private void cancelarEdicion() {
        limpiarCampos();
        Ui.habilitar(pnlFormulario, false);
    }

    private void nuevo() {
        tblCentros.clearSelection();
        limpiarCampos();
        Ui.habilitar(pnlFormulario, true);
        txtCodigo.requestFocusInWindow();
    }

    private void editar() {
        int fila = Ui.filaSeleccionada(this, tblCentros);
        if (fila < 0) {
            return;
        }
        editado = visibles.get(fila);
        txtCodigo.setText(editado.getCodigo());
        txtNombre.setText(editado.getNombre());
        txtDireccion.setText(editado.getDireccion());
        txtTelefono.setText(editado.getTelefono());
        txtCorreo.setText(editado.getCorreo());
        txtDirector.setText(editado.getDirector());
        lblLogo.setText(editado.getRutaLogo() == null ? "(sin logotipo)" : new File(editado.getRutaLogo()).getName());
        Ui.habilitar(pnlFormulario, true);
    }

    private void eliminar() {
        int fila = Ui.filaSeleccionada(this, tblCentros);
        if (fila < 0) {
            return;
        }
        CentroEducativo centro = visibles.get(fila);
        try {
            if (!Ui.confirmar(this, "¿Desea eliminar el centro «" + centro.getNombre() + "»?")) {
                return;
            }
            dao.eliminar(centro.getId());
            if (centro == editado) {
                cancelarEdicion();
            }
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "No se pudo eliminar el centro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String director = txtDirector.getText().trim();

        if (codigo.isEmpty()) {
            Ui.aviso(this, "Ingrese el código del centro.");
            txtCodigo.requestFocusInWindow();
            return;
        }
        if (nombre.isEmpty()) {
            Ui.aviso(this, "Ingrese el nombre del centro.");
            txtNombre.requestFocusInWindow();
            return;
        }
        if (direccion.isEmpty()) {
            Ui.aviso(this, "Ingrese la dirección del centro.");
            txtDireccion.requestFocusInWindow();
            return;
        }
        if (telefono.isEmpty()) {
            Ui.aviso(this, "Ingrese el teléfono del centro.");
            txtTelefono.requestFocusInWindow();
            return;
        }
        if (!telefono.matches("[0-9+() -]{7,20}")) {
            Ui.aviso(this, "El teléfono solo puede contener números, espacios, guiones, paréntesis o +, "
                    + "con una longitud de 7 a 20 caracteres.");
            txtTelefono.requestFocusInWindow();
            return;
        }

        CentroEducativo c = new CentroEducativo();
        c.setCodigo(codigo);
        c.setNombre(nombre);
        c.setDireccion(direccion);
        c.setTelefono(telefono);
        c.setCorreo(correo);
        c.setDirector(director);
        if (editado != null && editado.getRutaLogo() != null) {
            c.setRutaLogo(editado.getRutaLogo());
        }

        try {
            if (editado == null) {
                for (CentroEducativo existente : dao.listar()) {
                    if (existente.getCodigo().equalsIgnoreCase(codigo)) {
                        Ui.aviso(this, "Ya existe un centro con el código «" + codigo + "».");
                        txtCodigo.requestFocusInWindow();
                        return;
                    }
                }
                dao.insertar(c);
            } else {
                for (CentroEducativo existente : dao.listar()) {
                    if (existente.getId() != editado.getId()
                            && existente.getCodigo().equalsIgnoreCase(codigo)) {
                        Ui.aviso(this, "Ya existe un centro con el código «" + codigo + "».");
                        txtCodigo.requestFocusInWindow();
                        return;
                    }
                }
                editado.setCodigo(codigo);
                editado.setNombre(nombre);
                editado.setDireccion(direccion);
                editado.setTelefono(telefono);
                editado.setCorreo(correo);
                editado.setDirector(director);
                dao.actualizar(editado);
            }
            cancelarEdicion();
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seleccionarLogo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar logotipo institucional");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif", "bmp", "webp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        if (editado != null) {
            editado.setRutaLogo(archivo.getAbsolutePath());
        }
        lblLogo.setText(archivo.getName());
    }
}
