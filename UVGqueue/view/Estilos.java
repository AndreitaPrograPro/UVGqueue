package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/** Lenguaje visual compartido por todas las ventanas de UVGqueue. */
public final class Estilos {
    public static final Color VERDE_OSCURO = new Color(12, 79, 62);
    public static final Color VERDE_MEDIO = new Color(27, 169, 118);
    public static final Color VERDE_SUAVE = new Color(211, 241, 228);
    public static final Color MENTA = new Color(236, 249, 243);
    public static final Color CREMA = new Color(252, 249, 244);
    public static final Color AMARILLO = new Color(235, 165, 54);
    public static final Color CORAL = new Color(235, 103, 103);
    public static final Color FONDO = CREMA;
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(31, 45, 40);
    public static final Color TEXTO_SECUNDARIO = new Color(105, 119, 113);
    public static final Color BORDE = new Color(222, 229, 224);
    public static final Color ERROR = new Color(201, 67, 74);
    public static final Color SOMBRA = new Color(16, 55, 43, 30);

    private static final String FUENTE_TEXTO = elegirFuente(
            "Segoe UI", "Inter", "Arial", "SansSerif");
    private static final String FUENTE_CALIGRAFICA = elegirFuente(
            "Segoe Script", "Brush Script MT", "Monotype Corsiva", "Serif");

    private Estilos() {
    }

    public static void instalarLookAndFeel() {
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("OptionPane.messageFont", fuente(Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", fuente(Font.BOLD, 12));
        UIManager.put("ComboBox.font", fuente(Font.PLAIN, 13));
        UIManager.put("TextField.font", fuente(Font.PLAIN, 14));
        UIManager.put("PasswordField.font", fuente(Font.PLAIN, 14));
        UIManager.put("ToolTip.font", fuente(Font.PLAIN, 12));
    }

    public static Font fuente(int estilo, float tamanio) {
        return new Font(FUENTE_TEXTO, estilo, Math.round(tamanio));
    }

    public static Font fuenteCaligrafica(float tamanio) {
        return new Font(FUENTE_CALIGRAFICA, Font.PLAIN, Math.round(tamanio));
    }

    public static void estilizarBotonPrincipal(JButton boton) {
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(VERDE_MEDIO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(118, 42));
        boton.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(VERDE_OSCURO);
        campo.setMargin(new Insets(0, 4, 0, 4));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(10, 13, 10, 13)));
    }

    public static void estilizarScroll(JScrollPane scroll) {
        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.setUI(new BarraDesplazamientoUI());
        vertical.setPreferredSize(new Dimension(10, 0));
        vertical.setUnitIncrement(22);
        vertical.setBlockIncrement(110);
        scroll.setWheelScrollingEnabled(true);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setScrollMode(
                javax.swing.JViewport.BLIT_SCROLL_MODE);
    }

    public static Color mezclar(Color inicio, Color fin, float progreso) {
        float p = Math.max(0f, Math.min(1f, progreso));
        return new Color(
                Math.round(inicio.getRed() + (fin.getRed() - inicio.getRed()) * p),
                Math.round(inicio.getGreen() + (fin.getGreen() - inicio.getGreen()) * p),
                Math.round(inicio.getBlue() + (fin.getBlue() - inicio.getBlue()) * p),
                Math.round(inicio.getAlpha() + (fin.getAlpha() - inicio.getAlpha()) * p));
    }

    public static void mostrarProximamente(Component padre, String modulo) {
        JOptionPane.showMessageDialog(padre,
                "El módulo de '" + modulo + "' estará disponible próximamente.",
                "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }

    public static ImageIcon cargarIcono(String nombre, int ancho, int alto) {
        java.net.URL ruta = Estilos.class.getResource("/images/" + nombre);
        if (ruta == null) {
            return null;
        }
        ImageIcon icono = new ImageIcon(ruta);
        return new ImageIcon(icono.getImage().getScaledInstance(
                ancho, alto, java.awt.Image.SCALE_SMOOTH));
    }

    private static String elegirFuente(String... candidatas) {
        String[] disponibles = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        for (String candidata : candidatas) {
            for (String disponible : disponibles) {
                if (disponible.equalsIgnoreCase(candidata)) {
                    return disponible;
                }
            }
        }
        return candidatas[candidatas.length - 1];
    }
}
