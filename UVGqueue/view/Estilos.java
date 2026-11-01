package view;
import java.awt.*;
import javax.swing.*;
public class Estilos {
    // Paleta de colores UVGqueue
        public static final Color VERDE_OSCURO = new Color(22, 101, 52);
        public static final Color VERDE_MEDIO = new Color(34, 197, 94);
        public static final Color VERDE_SUAVE = new Color(187, 247, 208);
        public static final Color AMARILLO = new Color(255, 183, 3);
        public static final Color CORAL = new Color(251, 113, 133);
        public static final Color FONDO = new Color(248, 250, 252);
        public static final Color TEXTO = new Color(30, 41, 59);
        public static final Color TEXTO_SECUNDARIO = new Color(100, 116, 139);
        public static final Color BORDE = new Color(226, 232, 240);
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
        JOptionPane.showMessageDialog(padre,"El módulo de '" + modulo+"' estará disponible próximamente.","Próximamente",JOptionPane.INFORMATION_MESSAGE);
    }
    public static ImageIcon cargarIcono(String nombre, int ancho, int alto) {
        java.net.URL ruta = Estilos.class.getResource(
                "/images/" + nombre
        );

        if (ruta == null) {
                System.out.println("No se encontró la imagen: " + nombre);
                return null;
        }

        ImageIcon icono = new ImageIcon(ruta);

        Image imagen = icono.getImage().getScaledInstance(
                ancho,
                alto,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagen);
        }
}
