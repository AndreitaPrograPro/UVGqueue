package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.EstadoRestaurante;
import model.Restaurante;

public class RestauranteDetalleFrame extends JFrame {

    public RestauranteDetalleFrame(
            Restaurante restaurante,
            Integer promedio,
            List<Integer> tiempos
    ) {
        setTitle(restaurante.getNombre());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 720);
        setMinimumSize(new Dimension(700, 650));
        setLocationRelativeTo(null);

        JPanel contenido = new JPanel();
        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        contenido.add(
                crearEncabezado(
                        restaurante,
                        promedio
                )
        );

        contenido.add(
                Box.createVerticalStrut(20)
        );

        contenido.add(
                crearEspacioMenu()
        );

        contenido.add(
                Box.createVerticalStrut(20)
        );

        contenido.add(
                crearSeccionGrafica(tiempos)
        );

        JScrollPane scroll =
                new JScrollPane(contenido);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(
                Estilos.FONDO
        );
        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        add(scroll);
    }

    private JPanel crearEncabezado(
            Restaurante restaurante,
            Integer promedio
    ) {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );
        encabezado.setBackground(Color.WHITE);
        encabezado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        encabezado.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        180
                )
        );
        encabezado.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Estilos.BORDE
                        ),
                        new EmptyBorder(
                                22, 25, 22, 25
                        )
                )
        );

        boolean abierto =
                restaurante.getEstadoActual()
                == EstadoRestaurante.ABIERTO;

        JLabel nombre = crearEtiqueta(
                restaurante.getNombre(),
                Estilos.TEXTO,
                Font.BOLD,
                27
        );

        JLabel ubicacion = crearEtiqueta(
                restaurante.getUbicacion(),
                Estilos.TEXTO_SECUNDARIO,
                Font.PLAIN,
                14
        );

        JLabel estado = crearEtiqueta(
                obtenerTextoEstado(restaurante),
                abierto
                        ? Estilos.VERDE_OSCURO
                        : Estilos.ERROR,
                Font.BOLD,
                14
        );

        encabezado.add(nombre);
        encabezado.add(Box.createVerticalStrut(7));
        encabezado.add(ubicacion);
        encabezado.add(Box.createVerticalStrut(12));
        encabezado.add(estado);

        if (abierto) {
            JLabel tiempo = crearEtiqueta(
                    promedio == null
                            ? "Promedio aún no disponible"
                            : "Tiempo promedio: "
                              + promedio + " minutos",
                    Estilos.TEXTO,
                    Font.BOLD,
                    15
            );

            encabezado.add(
                    Box.createVerticalStrut(10)
            );
            encabezado.add(tiempo);
        }

        return encabezado;
    }

    /*
     * Por el momento este panel solamente reserva
     * el espacio donde posteriormente irá el menú.
     */
    private JPanel crearEspacioMenu() {
        JPanel panelMenu =
                new JPanel(new BorderLayout());

        panelMenu.setBackground(Color.WHITE);
        panelMenu.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        panelMenu.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        260
                )
        );
        panelMenu.setPreferredSize(
                new Dimension(700, 260)
        );
        panelMenu.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        Estilos.BORDE
                                ),
                                "Menú"
                        ),
                        new EmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        JLabel mensaje = new JLabel(
                "El menú del restaurante se mostrará aquí.",
                SwingConstants.CENTER
        );

        mensaje.setForeground(
                Estilos.TEXTO_SECUNDARIO
        );
        mensaje.setFont(
                Estilos.fuente(
                        Font.PLAIN,
                        15
                )
        );

        panelMenu.add(
                mensaje,
                BorderLayout.CENTER
        );

        return panelMenu;
    }

    private JPanel crearSeccionGrafica(
            List<Integer> tiempos
    ) {
        JPanel seccion =
                new JPanel(new BorderLayout());

        seccion.setBackground(Color.WHITE);
        seccion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        seccion.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        300
                )
        );
        seccion.setPreferredSize(
                new Dimension(700, 300)
        );
        seccion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        Estilos.BORDE
                                ),
                                "Tiempos de espera reportados"
                        ),
                        new EmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        seccion.add(
                new GraficaTiempoPanel(tiempos),
                BorderLayout.CENTER
        );

        return seccion;
    }

    private String obtenerTextoEstado(
            Restaurante restaurante
    ) {
        return switch (
                restaurante.getEstadoActual()
        ) {
            case ABIERTO ->
                "● ABIERTO";

            case POSIBLEMENTE_CERRADO ->
                "● POSIBLEMENTE CERRADO";

            default ->
                restaurante.estaEnHorario()
                        ? "● CERRADO"
                        : "● CERRADO POR HORARIO";
        };
    }

    private JLabel crearEtiqueta(
            String texto,
            Color color,
            int estilo,
            int tamaño
    ) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(color);
        etiqueta.setFont(
                Estilos.fuente(
                        estilo,
                        tamaño
                )
        );
        etiqueta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        return etiqueta;
    }

    /*
     * Panel encargado de dibujar la gráfica.
     */
    private static class GraficaTiempoPanel
            extends JPanel {

        private final List<Integer> tiempos;

        public GraficaTiempoPanel(
                List<Integer> tiempos
        ) {
            this.tiempos =
                    tiempos == null
                            ? new ArrayList<>()
                            : new ArrayList<>(tiempos);

            setOpaque(false);
            setPreferredSize(
                    new Dimension(650, 220)
            );
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (tiempos.isEmpty()) {
                dibujarMensajeVacio(g);
                g.dispose();
                return;
            }

            int margenIzquierdo = 50;
            int margenDerecho = 25;
            int margenSuperior = 25;
            int margenInferior = 40;

            int ancho = getWidth()
                    - margenIzquierdo
                    - margenDerecho;

            int alto = getHeight()
                    - margenSuperior
                    - margenInferior;

            int maximo = Math.max(
                    1,
                    tiempos.stream()
                            .mapToInt(Integer::intValue)
                            .max()
                            .orElse(1)
            );

            g.setColor(Estilos.TEXTO_SECUNDARIO);

            g.drawLine(
                    margenIzquierdo,
                    margenSuperior,
                    margenIzquierdo,
                    margenSuperior + alto
            );

            g.drawLine(
                    margenIzquierdo,
                    margenSuperior + alto,
                    margenIzquierdo + ancho,
                    margenSuperior + alto
            );

            g.drawString(
                    maximo + " min",
                    5,
                    margenSuperior + 5
            );

            g.drawString(
                    "0 min",
                    15,
                    margenSuperior + alto
            );

            g.setColor(Estilos.VERDE_MEDIO);
            g.setStroke(new BasicStroke(3));

            int xAnterior = 0;
            int yAnterior = 0;

            for (int i = 0; i < tiempos.size(); i++) {
                int tiempo = tiempos.get(i);

                int x = tiempos.size() == 1
                        ? margenIzquierdo + ancho / 2
                        : margenIzquierdo
                          + i * ancho
                          / (tiempos.size() - 1);

                int y = margenSuperior
                        + alto
                        - tiempo * alto / maximo;

                if (i > 0) {
                    g.drawLine(
                            xAnterior,
                            yAnterior,
                            x,
                            y
                    );
                }

                g.fillOval(
                        x - 5,
                        y - 5,
                        10,
                        10
                );

                g.drawString(
                        String.valueOf(tiempo),
                        x - 5,
                        y - 10
                );
                xAnterior = x;
                yAnterior = y;
            }
            g.setColor(Estilos.TEXTO_SECUNDARIO);
            g.drawString("Reportes del intervalo",margenIzquierdo+ ancho / 2- 60,getHeight() - 8);
            g.dispose();
        }
        private void dibujarMensajeVacio(
                Graphics2D g
        ) {
            String mensaje ="Aún no hay suficientes reportes "+ "para mostrar la gráfica.";
            FontMetrics metricas =g.getFontMetrics();
            int x = (getWidth()- metricas.stringWidth(mensaje)) / 2;
            int y = getHeight() / 2;
            g.setColor(Estilos.TEXTO_SECUNDARIO);
            g.drawString(mensaje, x, y);
        }
    }
}