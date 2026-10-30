package com.labcontrol.UI;

import com.labcontrol.dao.UsuarioDao;
import com.labcontrol.modelo.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PnlUsuarios extends javax.swing.JPanel implements Refrescable {

    private static final String ETIQUETA_CLAVE = "Contraseña";
    private static final String ETIQUETA_CLAVE_EDICION = "Contraseña (vacía = sin cambios)";

    private final UsuarioDao dao = new UsuarioDao();
    private final List<Usuario> visibles = new ArrayList<>();
    private DefaultTableModel modelo;
    private Usuario editado;
    private String ultimaBusqueda = "";
    private boolean cargando = true;

    public PnlUsuarios() {
        initComponents();
        iniciar();
    }

    private void iniciar() {
        Ui.estiloTitulo(lblTitulo);
        Ui.configurarTabla(tblUsuarios, 90, 260, 160, 200, 110);
        modelo = (DefaultTableModel) tblUsuarios.getModel();
        refrescar();
    }

    @Override
    public void refrescar() {
        cargando = false;
        cargarTabla();
        cancelarEdicion();
    }

    private void cargarTabla() {
        ultimaBusqueda = txtBusqueda.getText();
        String texto = ultimaBusqueda.trim().toLowerCase();
        Object estadoFiltro = cboFiltroEstado.getSelectedItem();
        visibles.clear();
        modelo.setRowCount(0);
        try {
            for (Usuario usuario : dao.listar()) {
                boolean coincideTexto = texto.isEmpty()
                        || usuario.getNombre().toLowerCase().contains(texto)
                        || usuario.getUsuario().toLowerCase().contains(texto);
                boolean coincideEstado = "Todos".equals(estadoFiltro) || usuario.isEstado()
                        && "Activo".equals(estadoFiltro)
                        || !usuario.isEstado() && "Inactivo".equals(estadoFiltro);
                if (coincideTexto && coincideEstado) {
                    visibles.add(usuario);
                    modelo.addRow(new Object[]{usuario.getIdUsuario(), usuario.getNombre(), usuario.getUsuario(),
                        usuario.getRol(), usuario.isEstado() ? "Activo" : "Inactivo"});
                }
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        editado = null;
        lblClave.setText(ETIQUETA_CLAVE);
        txtNombre.setText("");
        txtUsuario.setText("");
        txtClave.setText("");
        cboRol.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
    }

    private void cancelarEdicion() {
        limpiarCampos();
        Ui.habilitar(pnlFormulario, false);
    }

    private void nuevo() {
        tblUsuarios.clearSelection();
        limpiarCampos();
        Ui.habilitar(pnlFormulario, true);
        txtNombre.requestFocusInWindow();
    }

    private void editar() {
        int fila = Ui.filaSeleccionada(this, tblUsuarios);
        if (fila < 0) {
            return;
        }
        editado = visibles.get(fila);
        lblClave.setText(ETIQUETA_CLAVE_EDICION);
        txtNombre.setText(editado.getNombre());
        txtUsuario.setText(editado.getUsuario());
        txtClave.setText("");
        cboRol.setSelectedItem(editado.getRol());
        cboEstado.setSelectedItem(editado.isEstado() ? "Activo" : "Inactivo");
        Ui.habilitar(pnlFormulario, true);
    }

    private void eliminar() {
        int fila = Ui.filaSeleccionada(this, tblUsuarios);
        if (fila < 0) {
            return;
        }
        Usuario usuario = visibles.get(fila);
        try {
            if (usuario.getIdUsuario() == 1) {
                Ui.aviso(this, "No se puede eliminar el administrador del sistema.");
                return;
            }
            if (!Ui.confirmar(this, "¿Desea eliminar al usuario «" + usuario.getUsuario() + "»?")) {
                return;
            }
            dao.eliminar(usuario.getIdUsuario());
            if (usuario == editado) {
                cancelarEdicion();
            }
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "No se pudo eliminar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String nombreUsuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        String rol = (String) cboRol.getSelectedItem();
        boolean estado = "Activo".equals(cboEstado.getSelectedItem());

        if (nombre.isEmpty()) {
            Ui.aviso(this, "Ingrese el nombre del usuario.");
            txtNombre.requestFocusInWindow();
            return;
        }
        if (nombreUsuario.isEmpty() || nombreUsuario.matches(".*\\s.*")) {
            Ui.aviso(this, "Ingrese un nombre de usuario sin espacios.");
            txtUsuario.requestFocusInWindow();
            return;
        }
        if (editado == null && clave.isEmpty()) {
            Ui.aviso(this, "Ingrese la contraseña del usuario.");
            txtClave.requestFocusInWindow();
            return;
        }

        try {
            Usuario existente = dao.buscarPorNombreUsuario(nombreUsuario);
            if (existente != null && (editado == null || existente.getIdUsuario() != editado.getIdUsuario())) {
                Ui.aviso(this, "Ya existe otro usuario con el nombre de usuario «" + nombreUsuario + "».");
                txtUsuario.requestFocusInWindow();
                return;
            }
        } catch (SQLException e) {
            Ui.fallo(this, "Error al verificar usuario: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido("");
        u.setUsuario(nombreUsuario);
        u.setContrasena(clave.isEmpty() ? (editado != null ? editado.getContrasena() : "clave123") : clave);
        u.setCorreo("");
        u.setRol(rol);
        u.setEstado(estado);
        u.setFechaCreacion(java.time.LocalDate.now());

        try {
            if (editado == null) {
                dao.insertar(u);
            } else {
                u.setIdUsuario(editado.getIdUsuario());
                dao.actualizar(u);
            }
            cancelarEdicion();
            cargarTabla();
        } catch (SQLException e) {
            Ui.fallo(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblBuscar = new javax.swing.JLabel();
        txtBusqueda = new javax.swing.JTextField();
        lblFiltroEstado = new javax.swing.JLabel();
        cboFiltroEstado = new javax.swing.JComboBox<>();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        scrUsuarios = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        pnlFormulario = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JPasswordField();
        lblRol = new javax.swing.JLabel();
        cboRol = new javax.swing.JComboBox<>();
        lblEstado = new javax.swing.JLabel();
        cboEstado = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        lblTitulo.setText("Usuarios");

        lblBuscar.setText("Buscar (nombre o usuario):");

        txtBusqueda.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBusquedaCaretUpdate(evt);
            }
        });

        lblFiltroEstado.setText("Estado:");

        cboFiltroEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo" }));
        cboFiltroEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroEstadoActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo usuario");
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

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Usuario", "Rol", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return canEdit [columnIndex];

            }
        });
        scrUsuarios.setViewportView(tblUsuarios);

        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Usuario"));

        lblNombre.setText("Nombre");

        lblUsuario.setText("Usuario");

        lblClave.setText("Contraseña");

        lblRol.setText("Rol");

        cboRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Docente", "Encargado de laboratorio" }));

        lblEstado.setText("Estado");

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));

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
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClave)
                    .addComponent(txtClave, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRol)
                    .addComponent(cboRol, 0, 170, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEstado)
                    .addComponent(cboEstado, 0, 100, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(lblNombre)
                    .addComponent(lblUsuario)
                    .addComponent(lblClave)
                    .addComponent(lblRol)
                    .addComponent(lblEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(lblTitulo))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(lblFiltroEstado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrUsuarios)
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
                .addComponent(lblTitulo)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFiltroEstado)
                    .addComponent(cboFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    private void txtBusquedaCaretUpdate(javax.swing.event.CaretEvent evt) {
        if (modelo != null && !txtBusqueda.getText().equals(ultimaBusqueda)) {
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
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<String> cboFiltroEstado;
    private javax.swing.JComboBox<String> cboRol;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFiltroEstado;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JScrollPane scrUsuarios;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtUsuario;
}
