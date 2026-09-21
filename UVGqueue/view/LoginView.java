package view;

import java.awt.*;
import javax.swing.*;


public class LoginView extends JFrame {
    private static final String LOGIN = "LOGIN";
    private static final String REGISTRO = "REGISTRO";
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final JTextField txtLoginUser = new JTextField();
    private final JPasswordField txtLoginPassword = new JPasswordField();
    private final JButton btnSignIn = new JButton("Iniciar Sesión");
    private final JButton btnForgotPassword = new JButton("Olvidé mi contraseña");
    private final JButton btnGoToRegister = new JButton("Crear Cuenta");
    private final JTextField txtRegUser = new JTextField();
    private final JTextField txtRegEmail = new JTextField();
    private final JPasswordField txtRegPassword = new JPasswordField();
    private final JPasswordField txtRegConfirmPassword = new JPasswordField();
    private final JButton btnconfirmRegister = new JButton("Confirmar datos");
    private final JButton btnBackToLogin = new JButton("Volver al incio de sesión");
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
        container.add(crearPanelLogin(), LOGIN);
        container.add(crearPanelRegistro(), REGISTRO);
        add(container);
    }
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilos.FONDO);
        GridBagConstraints gbc = crearRestricciones();
        JLabel titulo = crearTitulo("Iniciar sesión");
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(titulo, gbc);
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.gridy = 1;
        panel.add(crearEtiqueta("Usuario o correo:"), gbc);
        gbc.gridy = 2;
        configurarCampo(txtLoginUser, 35);
        panel.add(txtLoginUser, gbc);
        gbc.gridy = 3;
        panel.add(crearEtiqueta("Contraseña:"), gbc);
        gbc.gridy = 4;
        configurarCampo(txtLoginPassword, 35);
        panel.add(txtLoginPassword, gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(18, 20, 10, 20);
        configurarBotonPrincipal(btnSignIn, 40);
        panel.add(btnSignIn, gbc);
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inferior.setOpaque(false);
        estilizarBotonEnlace(btnForgotPassword);
        estilizarBotonEnlace(btnGoToRegister);
        inferior.add(btnForgotPassword);
        inferior.add(new JLabel("|"));
        inferior.add(btnGoToRegister);
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 20, 20, 20);
        panel.add(inferior, gbc);
        return panel;
    }
    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilos.FONDO);
        GridBagConstraints gbc = crearRestricciones();

        gbc.gridy = 0;
        gbc.insets = new Insets(12, 20, 12, 20);
        panel.add(crearTitulo("Crear cuenta"), gbc);

        gbc.insets = new Insets(4, 20, 4, 20);
        agregarCampo(panel, gbc, 1, "Nombre de usuario:", txtRegUser);
        agregarCampo(panel, gbc, 3, "Correo electrónico:", txtRegEmail);
        agregarCampo(panel, gbc, 5, "Crear contraseña:", txtRegPassword);
        agregarCampo(panel, gbc, 7, "Confirmar contraseña:", txtRegConfirmPassword);
        gbc.gridy = 9;
        gbc.insets = new Insets(14, 20, 8, 20);
        configurarBotonPrincipal(btnconfirmRegister, 38);
        panel.add(btnconfirmRegister, gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(2, 20, 12, 20);
        estilizarBotonEnlace(btnBackToLogin);
        panel.add(btnBackToLogin, gbc);
        return panel;
    }
    private GridBagConstraints crearRestricciones() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.CENTER);
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.VERDE_OSCURO);
        return titulo;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Estilos.TEXTO);
        return etiqueta;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
                              String texto, JTextField campo) {
        gbc.gridy = fila;
        panel.add(crearEtiqueta(texto), gbc);
        gbc.gridy = fila + 1;
        configurarCampo(campo, 32);
        panel.add(campo, gbc);
    }

    private void configurarCampo(JTextField campo, int altura) {
        campo.setPreferredSize(new Dimension(280, altura));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void configurarBotonPrincipal(JButton boton, int altura) {
        boton.setPreferredSize(new Dimension(280, altura));
        boton.setBackground(Estilos.VERDE_MEDIO);
        boton.setForeground(Color.WHITE);
        boton.setFont(Estilos.fuente(Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonEnlace(JButton boton) {
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setForeground(Estilos.VERDE_OSCURO);
        boton.setFont(Estilos.fuente(Font.PLAIN, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void registrarEventosNavegacion() {
        btnGoToRegister.addActionListener(e -> mostrarRegistro());
        btnBackToLogin.addActionListener(e -> mostrarLogin());
    }

    public String getLoginUser() {
        return txtLoginUser.getText().trim();
    }

    public String getLoginPassword() {
        return new String(txtLoginPassword.getPassword());
    }

    public String getRegUser() {
        return txtRegUser.getText().trim();
    }

    public String getRegEmail() {
        return txtRegEmail.getText().trim();
    }

    public String getRegPassword() {
        return new String(txtRegPassword.getPassword());
    }

    public String getRegConfirmPassword() {
        return new String(txtRegConfirmPassword.getPassword());
    }

    public JButton getBtnSignIn() {
        return btnSignIn;
    }

    public JButton getBtnForgotPassword() {
        return btnForgotPassword;
    }

    public JButton getBtnConfirmRegister() {
        return btnconfirmRegister;
    }

    public void mostrarLogin() {
        cardLayout.show(container, LOGIN);
    }

    public void mostrarRegistro() {
        cardLayout.show(container, REGISTRO);
    }

    public void limpiarLogin() {
        txtLoginUser.setText("");
        txtLoginPassword.setText("");
    }

    public void limpiarRegistro() {
        txtRegUser.setText("");
        txtRegEmail.setText("");
        txtRegPassword.setText("");
        txtRegConfirmPassword.setText("");
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Error", JOptionPane.ERROR_MESSAGE
        );
    }
}