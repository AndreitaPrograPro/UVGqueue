package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/** Acceso y registro con el mismo lenguaje visual de la aplicación. */
public class LoginView extends JFrame {
    private static final String LOGIN = "LOGIN";
    private static final String REGISTRO = "REGISTRO";
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final JTextField txtLoginUser = new JTextField();
    private final JPasswordField txtLoginPassword = new JPasswordField();
    private final JButton btnSignIn = new BotonAnimado(
            "Entrar a UVGqueue", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
    private final JButton btnForgotPassword = new JButton("Olvidé mi contraseña");
    private final JButton btnGoToRegister = new JButton("Crear una cuenta");
    private final JTextField txtRegUser = new JTextField();
    private final JTextField txtRegEmail = new JTextField();
    private final JPasswordField txtRegPassword = new JPasswordField();
    private final JPasswordField txtRegConfirmPassword = new JPasswordField();
    private final JButton btnconfirmRegister = new BotonAnimado(
            "Crear mi cuenta", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
    private final JButton btnBackToLogin = new JButton("Ya tengo cuenta · Iniciar sesión");

    public LoginView() {
        Estilos.instalarLookAndFeel();
        configurarVentana();
        construirInterfaz();
        registrarEventosNavegacion();
    }

    private void configurarVentana() {
        setTitle("UVGqueue · Bienvenido");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(920, 640);
        setMinimumSize(new Dimension(860, 610));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(Estilos.FONDO);
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        cuerpo.setPreferredSize(new Dimension(850, 565));
        cuerpo.add(crearPanelMarca(), restriccionesMitad(0, 0.43));

        container.setOpaque(false);
        container.add(crearPanelLogin(), LOGIN);
        container.add(crearPanelRegistro(), REGISTRO);
        cuerpo.add(container, restriccionesMitad(1, 0.57));
        raiz.add(cuerpo);
        add(raiz);
    }

    private GridBagConstraints restriccionesMitad(int x, double peso) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = 0;
        gbc.weightx = peso;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private JPanel crearPanelMarca() {
        PanelRedondeado panel = new PanelRedondeado(34, Estilos.VERDE_OSCURO);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(36, 35, 30, 35));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("UVGqueue");
        logo.setFont(Estilos.fuenteCaligrafica(42));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel frase = new JLabel("Menos espera. Más campus.");
        frase.setFont(Estilos.fuente(Font.BOLD, 16));
        frase.setForeground(new Color(213, 244, 230));
        frase.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descripcion = new JLabel(
                "<html>Consulta filas, comparte tiempos<br>y encuentra tu próxima parada.</html>");
        descripcion.setFont(Estilos.fuente(Font.PLAIN, 13));
        descripcion.setForeground(new Color(183, 218, 202));
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(logo);
        textos.add(Box.createVerticalStrut(8));
        textos.add(frase);
        textos.add(Box.createVerticalStrut(9));
        textos.add(descripcion);

        IlustracionCampus ilustracion = new IlustracionCampus();
        panel.add(textos, BorderLayout.NORTH);
        panel.add(ilustracion, BorderLayout.CENTER);
        JLabel pie = new JLabel("✦  Una comunidad que cuida tu tiempo");
        pie.setFont(Estilos.fuente(Font.PLAIN, 11));
        pie.setForeground(new Color(169, 218, 195));
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelLogin() {
        JPanel fondo = crearFondoFormulario();
        PanelRedondeado tarjeta = crearTarjetaFormulario();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        agregarEncabezado(tarjeta, "Hola otra vez", "Ingresa para ver el campus en tiempo real.");
        agregarCampo(tarjeta, "Usuario o correo", txtLoginUser);
        agregarCampo(tarjeta, "Contraseña", txtLoginPassword);
        btnSignIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSignIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tarjeta.add(Box.createVerticalStrut(9));
        tarjeta.add(btnSignIn);
        estilizarBotonEnlace(btnForgotPassword);
        estilizarBotonEnlace(btnGoToRegister);
        btnForgotPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGoToRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(btnForgotPassword);
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(btnGoToRegister);
        fondo.add(tarjeta);
        return fondo;
    }

    private JPanel crearPanelRegistro() {
        JPanel fondo = crearFondoFormulario();
        PanelRedondeado tarjeta = crearTarjetaFormulario();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        agregarEncabezado(tarjeta, "Únete a la fila", "Crea tu cuenta para guardar favoritos.");
        agregarCampo(tarjeta, "Nombre de usuario", txtRegUser);
        agregarCampo(tarjeta, "Correo electrónico", txtRegEmail);
        agregarCampo(tarjeta, "Contraseña", txtRegPassword);
        agregarCampo(tarjeta, "Confirmar contraseña", txtRegConfirmPassword);
        btnconfirmRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnconfirmRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(btnconfirmRegister);
        estilizarBotonEnlace(btnBackToLogin);
        btnBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(btnBackToLogin);
        fondo.add(tarjeta);
        return fondo;
    }

    private JPanel crearFondoFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 0));
        return panel;
    }

    private PanelRedondeado crearTarjetaFormulario() {
        PanelRedondeado tarjeta = new PanelRedondeado(30, Color.WHITE);
        tarjeta.setPreferredSize(new Dimension(440, 535));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(28, 35, 30, 35));
        return tarjeta;
    }

    private void agregarEncabezado(JPanel panel, String titulo, String subtitulo) {
        JLabel marca = new JLabel("UVGqueue");
        marca.setFont(Estilos.fuenteCaligrafica(25));
        marca.setForeground(Estilos.VERDE_MEDIO);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel principal = new JLabel(titulo);
        principal.setFont(Estilos.fuente(Font.BOLD, 24));
        principal.setForeground(Estilos.TEXTO);
        principal.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ayuda = new JLabel(subtitulo);
        ayuda.setFont(Estilos.fuente(Font.PLAIN, 12));
        ayuda.setForeground(Estilos.TEXTO_SECUNDARIO);
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(marca);
        panel.add(Box.createVerticalStrut(3));
        panel.add(principal);
        panel.add(Box.createVerticalStrut(5));
        panel.add(ayuda);
        panel.add(Box.createVerticalStrut(17));
    }

    private void agregarCampo(JPanel panel, String texto, JTextField campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Estilos.fuente(Font.BOLD, 12));
        etiqueta.setForeground(Estilos.TEXTO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        Estilos.estilizarCampo(campo);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(11));
    }

    private void estilizarBotonEnlace(JButton boton) {
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setForeground(Estilos.VERDE_OSCURO);
        boton.setFont(Estilos.fuente(Font.BOLD, 11));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void registrarEventosNavegacion() {
        btnGoToRegister.addActionListener(e -> mostrarRegistro());
        btnBackToLogin.addActionListener(e -> mostrarLogin());
    }

    public String getLoginUser() { return txtLoginUser.getText().trim(); }
    public String getLoginPassword() { return new String(txtLoginPassword.getPassword()); }
    public String getRegUser() { return txtRegUser.getText().trim(); }
    public String getRegEmail() { return txtRegEmail.getText().trim(); }
    public String getRegPassword() { return new String(txtRegPassword.getPassword()); }
    public String getRegConfirmPassword() {
        return new String(txtRegConfirmPassword.getPassword());
    }
    public JButton getBtnSignIn() { return btnSignIn; }
    public JButton getBtnForgotPassword() { return btnForgotPassword; }
    public JButton getBtnConfirmRegister() { return btnconfirmRegister; }
    public void mostrarLogin() { cardLayout.show(container, LOGIN); }
    public void mostrarRegistro() { cardLayout.show(container, REGISTRO); }
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
        DialogoAmigable.mostrar(this, titulo, mensaje, false);
    }
    public void mostrarError(String mensaje) {
        DialogoAmigable.mostrar(this, "Revisa tus datos", mensaje, true);
    }
}
