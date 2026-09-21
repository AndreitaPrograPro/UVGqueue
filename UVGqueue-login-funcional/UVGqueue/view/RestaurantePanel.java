package view;

import model.Fila;
import model.Restaurante;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Tarjeta que presenta la información principal de un restaurante. */
public class RestaurantePanel extends JPanel {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private final Restaurante restaurante;
    private final Fila fila;

    public RestaurantePanel(Restaurante restaurante, Fila fila) {
        this.restaurante = restaurante;
        this.fila = fila;
        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        setPreferredSize(new Dimension(360, 190));
        construirContenido();
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
        informacion.add(crearLinea("Ubicación", restaurante.getUbicacion()));
        informacion.add(Box.createVerticalStrut(8));
        informacion.add(crearLinea("Horario", obtenerHorario()));
        informacion.add(Box.createVerticalStrut(8));
        informacion.add(crearLinea("Fila", obtenerEstadoFila()));

        JButton detalle = new JButton("Ver detalles");
        Estilos.estilizarBotonPrincipal(detalle);
        detalle.setOpaque(true);
        detalle.setContentAreaFilled(true);
        detalle.setBorderPainted(false);
        detalle.addActionListener(evento -> mostrarDetalle());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(detalle);

        add(encabezado, BorderLayout.NORTH);
        add(informacion, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    private JPanel crearLinea(String titulo, String contenido) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linea.setOpaque(false);
        JLabel etiqueta = new JLabel(titulo + ":");
        etiqueta.setFont(Estilos.fuente(Font.BOLD, 12));
        etiqueta.setForeground(Estilos.TEXTO_SECUNDARIO);
        JLabel valor = new JLabel(contenido);
        valor.setFont(Estilos.fuente(Font.PLAIN, 12));
        valor.setForeground(Estilos.TEXTO);
        linea.add(etiqueta);
        linea.add(valor);
        return linea;
    }

    private JLabel crearEstado() {
        boolean abierta = estaAbierta();
        JLabel estado = new JLabel(abierta ? "ABIERTA" : "CERRADA");
        estado.setOpaque(true);
        estado.setFont(Estilos.fuente(Font.BOLD, 10));
        estado.setForeground(abierta ? new Color(22, 101, 52) : Estilos.ERROR);
        estado.setBackground(abierta ? new Color(220, 252, 231) : new Color(254, 226, 226));
        estado.setBorder(BorderFactory.createEmptyBorder(6, 9, 6, 9));
        return estado;
    }

    private boolean estaAbierta() {
        return restaurante.isActivo() && fila != null
                && "ABIERTA".equalsIgnoreCase(fila.getEstado());
    }

    private String obtenerHorario() {
        return restaurante.getHoraApertura().format(FORMATO_HORA)
                + " - " + restaurante.getHoraCierre().format(FORMATO_HORA);
    }

    private String obtenerEstadoFila() {
        if (!restaurante.isActivo()) return "Restaurante inactivo";
        return fila == null ? "Sin información" : fila.getEstado();
    }

    private void mostrarDetalle() {
        JOptionPane.showMessageDialog(
                this,
                restaurante.getNombre()
                        + "\n\nUbicación: " + restaurante.getUbicacion()
                        + "\nHorario: " + obtenerHorario()
                        + "\nEstado de fila: " + obtenerEstadoFila(),
                "Información del restaurante",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
