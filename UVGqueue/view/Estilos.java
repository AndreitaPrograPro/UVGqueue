package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

/** Lenguaje visual compartido por todas las ventanas de FastQueue. */
public final class Estilos {
    public static final Color AZAFRAN = new Color(244, 180, 0);
    public static final Color TOMATE = new Color(232, 93, 63);
    public static final Color MOSTAZA = new Color(166, 111, 0);
    public static final Color ESPRESSO = new Color(59, 42, 32);
    public static final Color CREMA = new Color(255, 247, 230);

    // Alias semánticos conservados para no alterar la lógica de las vistas.
    public static final Color VERDE_OSCURO = MOSTAZA;
    public static final Color VERDE_MEDIO = AZAFRAN;
    public static final Color VERDE_SUAVE = new Color(225, 235, 216);
    public static final Color MENTA = new Color(246, 239, 211);
    public static final Color AMARILLO = AZAFRAN;
    public static final Color CORAL = TOMATE;
    public static final Color FONDO = CREMA;
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = ESPRESSO;
    public static final Color TEXTO_SECUNDARIO = new Color(119, 91, 73);
    public static final Color BORDE = new Color(231, 216, 193);
    public static final Color ERROR = TOMATE;
    public static final Color SOMBRA = new Color(59, 42, 32, 30);

    private static final String FUENTE_TEXTO = elegirFuente(
            "Segoe UI", "Inter", "Arial", "SansSerif");
    private static final String FUENTE_TITULO = elegirFuente(
            "Georgia", "Cambria", "Palatino Linotype", "Serif");

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

    public static Font fuenteTitulo(float tamanio) {
        return new Font(FUENTE_TITULO, Font.BOLD, Math.round(tamanio));
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

    public static ImageIcon cargarIcono(String nombre, int ancho, int alto,
                                        float opacidad) {
        ImageIcon base = cargarIcono(nombre, ancho, alto);
        if (base == null) return null;
        BufferedImage imagen = new BufferedImage(
                ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                Math.max(0f, Math.min(1f, opacidad))));
        g.drawImage(base.getImage(), 0, 0, null);
        g.dispose();
        return new ImageIcon(imagen);
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
