package view;

import java.awt.*;
import javax.swing.*;

public class Estilos {

    // Paleta de colores UVGqueue
    public static final Color VERDE_OSCURO = new Color(30, 58, 42);
    public static final Color VERDE_MEDIO = new Color(46, 89, 65);
    public static final Color VERDE_SUAVE = new Color(190, 222, 207);
    public static final Color FONDO = new Color(245, 247, 248);
    public static final Color TEXTO = new Color(33, 37, 41);
    public static final Color TEXTO_SECUNDARIO =
            new Color(108, 117, 125);
    public static final Color BORDE = new Color(220, 224, 230);
    public static final Color ERROR = new Color(220, 53, 69);

    public static Font fuente(int estilo, float tamanio) {
        return new Font("SansSerif", estilo, (int) tamanio);
    }

    public static void estilizarBotonPrincipal(JButton boton) {
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(VERDE_MEDIO);
        boton.setFocusPainted(false);
        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        boton.setPreferredSize(new Dimension(110, 42));
    }

    public static void mostrarProximamente(
            Component padre,
            String modulo
    ) {
        JOptionPane.showMessageDialog(
                padre,
                "El módulo de '" + modulo
                        + "' estará disponible próximamente.",
                "Próximamente",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
