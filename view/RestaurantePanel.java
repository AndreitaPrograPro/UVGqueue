package view;

import model.Fila;
import model.Restaurante;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Tarjeta visual de un restaurante. */
public class RestaurantePanel extends JPanel {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private final Restaurante restaurante;
    private final Fila fila;

    public RestaurantePanel(Restaurante restaurante, Fila fila) {
        this.restaurante = restaurante;
        this.fila = fila;
        setOpaque(false);
        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        setPreferredSize(new Dimension(360, 205));
        construirContenido();
    }

    private void construirContenido() {
        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setOpaque(false);

        JLabel inicial = new JLabel(restaurante.getNombre().substring(0, 1).toUpperCase(), JLabel.CENTER);
        inicial.setOpaque(true);
        inicial.setBackground(Estilos.VERDE_CLARO);
        inicial.setForeground(Estilos.VERDE_OSCURO);
        inicial.setFont(Estilos.fuente(Font.BOLD, 22));
        inicial.setPreferredSize(new Dimension(52, 52));

        JPanel nombres = new JPanel();
        nombres.setOpaque(false);
        nombres.setLayout(new BoxLayout(nombres, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel(restaurante.getNombre());
        nombre.setFont(Estilos.fuente(Font.BOLD, 18));
        nombre.setForeground(Estilos.TEXTO);
        JLabel ubicacion = new JLabel(restaurante.getUbicacion());
        ubicacion.setFont(Estilos.fuente(Font.PLAIN, 12));
        ubicacion.setForeground(Estilos.TEXTO_SECUNDARIO);
        nombres.add(Box.createVerticalStrut(5));
        nombres.add(nombre);
        nombres.add(Box.createVerticalStrut(4));
        nombres.add(ubicacion);

        cabecera.add(inicial, BorderLayout.WEST);
        cabecera.add(nombres, BorderLayout.CENTER);
        cabecera.add(crearEtiquetaEstado(), BorderLayout.EAST);

        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        datos.setOpaque(false);
        datos.add(crearDato("Horario", obtenerHorario()));
        datos.add(crearDato("Estado de fila", obtenerEstadoFila()));

        JButton botonDetalle = new JButton("Ver detalles");
        Estilos.estilizarBotonPrincipal(botonDetalle);
        botonDetalle.addActionListener(e -> mostrarDetalle());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(botonDetalle);

        add(cabecera, BorderLayout.NORTH);
        add(datos, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    private JPanel crearDato(String titulo, String valor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel(titulo);
        nombre.setFont(Estilos.fuente(Font.PLAIN, 11));
        nombre.setForeground(Estilos.TEXTO_SECUNDARIO);
        JLabel contenido = new JLabel(valor);
        contenido.setFont(Estilos.fuente(Font.BOLD, 13));
        contenido.setForeground(Estilos.TEXTO);
        panel.add(nombre);
        panel.add(Box.createVerticalStrut(3));
        panel.add(contenido);
        return panel;
    }

    private JLabel crearEtiquetaEstado() {
        boolean abierta = estaAbierta();
        JLabel etiqueta = new JLabel(abierta ? "ABIERTA" : "CERRADA");
        etiqueta.setOpaque(true);
        etiqueta.setFont(Estilos.fuente(Font.BOLD, 10));
        etiqueta.setForeground(abierta ? Estilos.EXITO : Estilos.ERROR);
        etiqueta.setBackground(abierta ? Estilos.EXITO_FONDO : Estilos.ERROR_FONDO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
        return etiqueta;
    }

    private boolean estaAbierta() {
        return restaurante.isActivo() && fila != null
                && "ABIERTA".equalsIgnoreCase(fila.getEstado());
    }

    private String obtenerEstadoFila() {
        if (!restaurante.isActivo()) return "Restaurante inactivo";
        return fila == null ? "Sin información" : fila.getEstado();
    }

    private String obtenerHorario() {
        return restaurante.getHoraApertura().format(FORMATO_HORA)
                + " - " + restaurante.getHoraCierre().format(FORMATO_HORA);
    }

    private void mostrarDetalle() {
        String detalle = restaurante.getNombre()
                + "\n\nUbicación: " + restaurante.getUbicacion()
                + "\nHorario: " + obtenerHorario()
                + "\nEstado de fila: " + obtenerEstadoFila();
        JOptionPane.showMessageDialog(this, detalle,
                "Información del restaurante", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.setColor(Estilos.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
        super.paintComponent(graphics);
    }
}
