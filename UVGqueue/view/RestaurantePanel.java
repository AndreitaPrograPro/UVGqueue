package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.EstadoRestaurante;
import model.Restaurante;

/** Tarjeta reutilizable compatible con el modelo actual de Restaurante. */
public class RestaurantePanel extends JPanel {

    private final Restaurante restaurante;

    public RestaurantePanel(Restaurante restaurante) {
        this.restaurante = restaurante;
        configurarPanel();
        construirContenido();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        setPreferredSize(new Dimension(360, 155));
    }

    private void construirContenido() {
        JPanel encabezado = new JPanel(new BorderLayout(10, 0));
        encabezado.setOpaque(false);

        JLabel nombre = new JLabel(restaurante.getNombre());
        nombre.setFont(Estilos.fuente(Font.BOLD, 18));
        nombre.setForeground(Estilos.TEXTO);

        encabezado.add(nombre, BorderLayout.CENTER);
        encabezado.add(crearEstado(), BorderLayout.EAST);

        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(new BoxLayout(informacion, BoxLayout.Y_AXIS));
        informacion.add(crearDato("Ubicación", restaurante.getUbicacion()));
        informacion.add(Box.createVerticalStrut(9));
        informacion.add(crearDato("Estado", textoEstado()));

        add(encabezado, BorderLayout.NORTH);
        add(informacion, BorderLayout.CENTER);
    }

    private JPanel crearDato(String titulo, String valor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(Estilos.fuente(Font.PLAIN, 11));
        etiqueta.setForeground(Estilos.TEXTO_SECUNDARIO);

        JLabel contenido = new JLabel(valor);
        contenido.setFont(Estilos.fuente(Font.BOLD, 13));
        contenido.setForeground(Estilos.TEXTO);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(3));
        panel.add(contenido);
        return panel;
    }

    private JLabel crearEstado() {
        boolean abierto = restaurante.getEstado() == EstadoRestaurante.ABIERTO;
        JLabel estado = new JLabel(abierto ? "ABIERTO" : "CERRADO");
        estado.setOpaque(true);
        estado.setFont(Estilos.fuente(Font.BOLD, 10));
        estado.setForeground(
                abierto ? new Color(22, 101, 52) : Estilos.ERROR
        );
        estado.setBackground(
                abierto
                        ? new Color(220, 252, 231)
                        : new Color(254, 226, 226)
        );
        estado.setBorder(BorderFactory.createEmptyBorder(6, 9, 6, 9));
        return estado;
    }

    private String textoEstado() {
        return restaurante.getEstado() == EstadoRestaurante.ABIERTO
                ? "Disponible para reportes"
                : "No disponible";
    }
}
