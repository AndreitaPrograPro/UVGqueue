package view;

import javax.swing.*;
import java.awt.*;

/** Paleta y estilos compartidos por las vistas de UVGqueue. */
public final class Estilos {
    public static final Color VERDE_OSCURO = new Color(24, 86, 65);
    public static final Color VERDE_MEDIO = new Color(37, 122, 89);
    public static final Color VERDE_CLARO = new Color(222, 241, 232);
    public static final Color VERDE_SUAVE = new Color(177, 218, 199);
    public static final Color FONDO = new Color(246, 248, 247);
    public static final Color TEXTO = new Color(32, 43, 39);
    public static final Color TEXTO_SECUNDARIO = new Color(101, 116, 110);
    public static final Color BORDE = new Color(222, 229, 225);
    public static final Color EXITO = new Color(27, 120, 78);
    public static final Color EXITO_FONDO = new Color(224, 244, 234);
    public static final Color ERROR = new Color(177, 54, 54);
    public static final Color ERROR_FONDO = new Color(252, 232, 232);

    private Estilos() { }

    public static Font fuente(int estilo, int tamano) {
        return new Font("SansSerif", estilo, tamano);
    }

    public static void estilizarBotonPrincipal(JButton boton) {
        boton.setFont(fuente(Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(VERDE_MEDIO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void mostrarProximamente(Component padre, String seccion) {
        JOptionPane.showMessageDialog(padre,
                "La sección \"" + seccion + "\" estará disponible próximamente.",
                "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }
}
