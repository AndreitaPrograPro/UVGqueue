package view;

import java.awt.*;
import javax.swing.*;

/**
 * Vista para el inicio de sesión y registro de usuarios en Swing.
 * Mantiene la paleta de colores institucional de UVGqueue.
 */
public class LoginView extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    // Componentes del formulario de Login
    private final JTextField txtLoginUser = new JTextField();
    private final JPasswordField txtLoginPassword = new JPasswordField();
    private final JButton btnSignIn = new JButton("Iniciar sesión");
    private final JButton btnForgotPassword = new JButton("Olvidé mi contraseña");
    private final JButton btnGoToRegister = new JButton("Crear cuenta");

    // Componentes del formulario de Registro
    private final JTextField txtRegUser = new JTextField();
    private final JTextField txtRegEmail = new JTextField();
    private final JPasswordField txtRegPassword = new JPasswordField();
    private final JButton btnConfirmRegister = new JButton("Confirmar datos");
    private final JButton btnBackToLogin = new JButton("Volver al inicio de sesión");

    public LoginView() {
        configurarVentana();
        construirInterfaz();
        registrarEventosNavegacion();
    }

    private void configurarVentana() {
        setTitle("UVGqueue - Autenticación");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420, 480);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        container.add(crearPanelLogin(), "LOGIN");
        container.add(crearPanelRegistro(), "REGISTRO");
        add(container);
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilos.FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitle.setFont(Estilos.fuente(Font.BOLD, 22));
        lblTitle.setForeground(Estilos.VERDE_OSCURO);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(lblTitle, gbc);

        gbc.insets = new Insets(6, 20, 6, 20);
        
        // Campo Usuario
        gbc.gridy = 1;
        JLabel lblUser = new JLabel("Usuario o Correo:");
        lblUser.setForeground(Estilos.TEXTO);
        panel.add(lblUser, gbc);
        
        gbc.gridy = 2;
        txtLoginUser.setPreferredSize(new Dimension(280, 35));
        txtLoginUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(txtLoginUser, gbc);

        // Campo Contraseña
        gbc.gridy = 3;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Estilos.TEXTO);
        panel.add(lblPass, gbc);

        gbc.gridy = 4;
        txtLoginPassword.setPreferredSize(new Dimension(280, 35));
        txtLoginPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(txtLoginPassword, gbc);

        // Botón Sign In (Usando el verde medio)
        gbc.gridy = 5;
        gbc.insets = new Insets(18, 20, 10, 20);
        btnSignIn.setPreferredSize(new Dimension(280, 40));
        btnSignIn.setBackground(Estilos.VERDE_MEDIO);
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setFont(Estilos.fuente(Font.BOLD, 14));
        btnSignIn.setFocusPainted(false);
        btnSignIn.setOpaque(true);
        btnSignIn.setContentAreaFilled(true);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnSignIn, gbc);

        // Botones inferiores (Olvidé contraseña y Crear cuenta)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelInferior.setOpaque(false);

        estilizarBotonEnlace(btnForgotPassword);
        estilizarBotonEnlace(btnGoToRegister);

        panelInferior.add(btnForgotPassword);
        panelInferior.add(new JLabel("|"));
        panelInferior.add(btnGoToRegister);

        gbc.gridy = 6;
        gbc.insets = new Insets(5, 20, 20, 20);
        panel.add(panelInferior, gbc);

        return panel;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilos.FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Crear Cuenta", SwingConstants.CENTER);
        lblTitle.setFont(Estilos.fuente(Font.BOLD, 22));
        lblTitle.setForeground(Estilos.VERDE_OSCURO);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 20, 15, 20);
        panel.add(lblTitle, gbc);

        gbc.insets = new Insets(4, 20, 4, 20);

        // Entradas: Usuario, Correo, Contraseña
        gbc.gridy = 1; panel.add(new JLabel("Nombre de usuario:"), gbc);
        gbc.gridy = 2; txtRegUser.setPreferredSize(new Dimension(280, 32)); panel.add(txtRegUser, gbc);

        gbc.gridy = 3; panel.add(new JLabel("Correo electrónico:"), gbc);
        gbc.gridy = 4; txtRegEmail.setPreferredSize(new Dimension(280, 32)); panel.add(txtRegEmail, gbc);

        gbc.gridy = 5; panel.add(new JLabel("Crear contraseña:"), gbc);
        gbc.gridy = 6; txtRegPassword.setPreferredSize(new Dimension(280, 32)); panel.add(txtRegPassword, gbc);

        // Botón Confirmar Datos (Usando verde medio)
        gbc.gridy = 7;
        gbc.insets = new Insets(16, 20, 8, 20);
        btnConfirmRegister.setPreferredSize(new Dimension(280, 38));
        btnConfirmRegister.setBackground(Estilos.VERDE_MEDIO);
        btnConfirmRegister.setForeground(Color.WHITE);
        btnConfirmRegister.setFont(Estilos.fuente(Font.BOLD, 14));
        btnConfirmRegister.setFocusPainted(false);
        btnConfirmRegister.setOpaque(true);
        btnConfirmRegister.setContentAreaFilled(true);
        btnConfirmRegister.setBorderPainted(false);
        btnConfirmRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnConfirmRegister, gbc);

        // Botón Volver
        gbc.gridy = 8;
        gbc.insets = new Insets(2, 20, 15, 20);
        estilizarBotonEnlace(btnBackToLogin);
        panel.add(btnBackToLogin, gbc);

        return panel;
    }

    private void estilizarBotonEnlace(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setForeground(Estilos.VERDE_OSCURO);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(Estilos.fuente(Font.PLAIN, 12));
    }

    private void registrarEventosNavegacion() {
        btnGoToRegister.addActionListener(e -> cardLayout.show(container, "REGISTRO"));
        btnBackToLogin.addActionListener(e -> cardLayout.show(container, "LOGIN"));
    }

    public String getLoginUser() { return txtLoginUser.getText(); }
    public String getLoginPassword() { return new String(txtLoginPassword.getPassword()); }
    public String getRegUser() { return txtRegUser.getText(); }
    public String getRegEmail() { return txtRegEmail.getText(); }
    public String getRegPassword() { return new String(txtRegPassword.getPassword()); }

    public JButton getBtnSignIn() { return btnSignIn; }
    public JButton getBtnForgotPassword() { return btnForgotPassword; }
    public JButton getBtnConfirmRegister() { return btnConfirmRegister; }

    public void setProcesando(boolean procesando, String texto) {
        btnSignIn.setEnabled(!procesando);
        btnSignIn.setText(texto);
        txtLoginUser.setEnabled(!procesando);
        txtLoginPassword.setEnabled(!procesando);
    }

    public void setRegistroProcesando(boolean procesando) {
        btnConfirmRegister.setEnabled(!procesando);
        btnConfirmRegister.setText(procesando ? "Creando cuenta..." : "Confirmar datos");
    }

    public void limpiarContrasena() {
        txtLoginPassword.setText("");
        txtLoginPassword.requestFocusInWindow();
    }

    public void mostrarLogin() {
        txtLoginUser.setText(txtRegEmail.getText().trim());
        txtLoginPassword.setText("");
        txtRegUser.setText("");
        txtRegEmail.setText("");
        txtRegPassword.setText("");
        cardLayout.show(container, "LOGIN");
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo completar la operación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}
