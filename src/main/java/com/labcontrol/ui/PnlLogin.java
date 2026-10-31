package com.labcontrol.UI;

import com.labcontrol.dao.*;
import com.labcontrol.modelo.*;

import java.awt.Font;
import java.util.Arrays;
import java.util.function.Consumer;
import java.time.LocalDateTime;
import java.net.InetAddress;
import javax.swing.JOptionPane;

public class PnlLogin extends javax.swing.JPanel {

    private final UsuarioDao dao = new UsuarioDao();
    private final Consumer<Usuario> alIngresar;

    public PnlLogin(Consumer<Usuario> alIngresar) {
        this.alIngresar = alIngresar;
        initComponents();
        iniciar();
    }

    private void iniciar() {
        lblSistema.setFont(lblSistema.getFont().deriveFont(Font.BOLD, 24f));
        lblIniciar.setFont(lblIniciar.getFont().deriveFont(Font.BOLD, 28f));
        lblAcceso.setFont(lblAcceso.getFont().deriveFont(16f));
    }

    private void ingresar() {
        String nombreUsuario = txtUsuario.getText().trim();
        char[] caracteres = txtClave.getPassword();
        String clave = new String(caracteres);

        if (nombreUsuario.isEmpty() || clave.isEmpty()) {
            lblError.setText("Ingrese el nombre de usuario y la contraseña.");
            return;
        }
        try {
            Usuario usuario = dao.buscarPorNombreUsuario(nombreUsuario);
            if (usuario == null || !usuario.getContrasena().equals(clave)) {
                txtClave.setText("");
                lblError.setText("Usuario o contraseña incorrectos.");
                return;
            }
            if (!usuario.isEstado()) {
                txtClave.setText("");
                lblError.setText("El usuario está inactivo y no puede ingresar.");
                return;
            }
            registrarBitacora(usuario);
            alIngresar.accept(usuario);
        } catch (Exception e) {
            lblError.setText("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    private void registrarBitacora(Usuario usuario) {
        try {
            BitacoraAcceso registro = new BitacoraAcceso();
            registro.setFechaHora(LocalDateTime.now());
            registro.setAccion("INICIO SESION - " + usuario.getUsuario()
                    + " (" + usuario.getRol() + ")");
            
            try {
                registro.setIp(InetAddress.getLocalHost().getHostAddress()); 
            } catch (Exception ipError) {
                registro.setIp("localhost");
            }
            
            registro.setDispositivo("Aplicacion de escritorio");
            new BitacoraAccesoDao().insertar(registro);
        } catch (Exception ignorado) {
            JOptionPane.showMessageDialog(this, "error: " + ignorado);
        }
    }

    public void limpiar() {
        txtUsuario.setText("");
        txtClave.setText("");
        lblError.setText(" ");
        txtUsuario.requestFocusInWindow();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblSistema = new javax.swing.JLabel();
        pnlTarjeta = new javax.swing.JPanel();
        lblIniciar = new javax.swing.JLabel();
        lblAcceso = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JPasswordField();
        lblError = new javax.swing.JLabel();
        btnIngresar = new javax.swing.JButton();
        lblAyuda = new javax.swing.JLabel();

//        lblSistema.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
//        lblSistema.setText("     Sistema GEST-LAB");

        lblIniciar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIniciar.setText("Iniciar sesión");

        lblAcceso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAcceso.setText("Acceso al sistema");

        lblUsuario.setText("Nombre de usuario");

        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        lblClave.setText("Contraseña");

        txtClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClaveActionPerformed(evt);
            }
        });

        lblError.setForeground(new java.awt.Color(180, 30, 30));
        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText(" ");

        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });

        lblAyuda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAyuda.setText("Ingrese con su usuario y contraseña");

        javax.swing.GroupLayout pnlTarjetaLayout = new javax.swing.GroupLayout(pnlTarjeta);
        pnlTarjeta.setLayout(pnlTarjetaLayout);
        pnlTarjetaLayout.setHorizontalGroup(
            pnlTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTarjetaLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(pnlTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIniciar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAcceso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUsuario)
                    .addComponent(lblClave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtClave)
                    .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIngresar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAyuda, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );
        pnlTarjetaLayout.setVerticalGroup(
            pnlTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTarjetaLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblIniciar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAcceso)
                .addGap(22, 22, 22)
                .addComponent(lblUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblClave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIngresar)
                .addGap(14, 14, 14)
                .addComponent(lblAyuda)
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSistema)
                    .addComponent(pnlTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblSistema)
                .addGap(24, 24, 24)
                .addComponent(pnlTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {
        txtClave.requestFocusInWindow();
    }

    private void txtClaveActionPerformed(java.awt.event.ActionEvent evt) {
        ingresar();
    }

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {
        ingresar();
    }

    private javax.swing.JButton btnIngresar;
    private javax.swing.JLabel lblAcceso;
    private javax.swing.JLabel lblAyuda;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblIniciar;
    private javax.swing.JLabel lblSistema;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlTarjeta;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JTextField txtUsuario;
}
