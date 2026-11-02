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
            List<Integer> promediosPorIntervalo
    ) {
        setTitle(restaurante.getNombre());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 760);
        setMinimumSize(new Dimension(750, 680));
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

        contenido.add(crearEspacioMenu());

        contenido.add(
                Box.createVerticalStrut(20)
        );

        contenido.add(
                crearSeccionGrafica(
                        promediosPorIntervalo
                )
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

        EstadoRestaurante estadoActual =
                restaurante.getEstadoActual();

        boolean abierto =
                estadoActual
                == EstadoRestaurante.ABIERTO;

        Color colorEstado;

        if (abierto) {
            colorEstado =
                    Estilos.VERDE_OSCURO;

        } else if (
                estadoActual
                == EstadoRestaurante.POSIBLEMENTE_CERRADO
        ) {
            colorEstado =
                    Estilos.AMARILLO;

        } else {
            colorEstado =
                    Estilos.ERROR;
        }

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
                colorEstado,
                Font.BOLD,
                14
        );

        encabezado.add(nombre);
        encabezado.add(
                Box.createVerticalStrut(7)
        );
        encabezado.add(ubicacion);
        encabezado.add(
                Box.createVerticalStrut(12)
        );
        encabezado.add(estado);

        if (abierto) {
            JLabel tiempo = crearEtiqueta(
                    promedio == null
                            ? "Promedio aún no disponible"
                            : "Tiempo promedio: "
                              + promedio
                              + " minutos",
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
                new Dimension(750, 260)
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
                "El menú del restaurante "
                + "se mostrará aquí.",
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
            List<Integer> promedios
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
                        340
                )
        );

        seccion.setPreferredSize(
                new Dimension(750, 340)
        );

        seccion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        Estilos.BORDE
                                ),
                                "Promedio de espera "
                                + "por intervalo"
                        ),
                        new EmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        seccion.add(
                new GraficaBarrasPanel(promedios),
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
        JLabel etiqueta =
                new JLabel(texto);

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

    private static class GraficaBarrasPanel
            extends JPanel {

        private static final String[] INTERVALOS = {
            "07-08",
            "08-09",
            "09-10",
            "10-11",
            "11-12",
            "12-13",
            "13-14",
            "14-15",
            "15-16",
            "16-17",
            "17-18",
            "18-19",
            "19-20"
        };

        private final List<Integer> promedios;

        public GraficaBarrasPanel(
                List<Integer> promedios
        ) {
            this.promedios =
                    promedios == null
                            ? new ArrayList<>()
                            : new ArrayList<>(
                                    promedios
                            );

            setOpaque(false);

            setPreferredSize(
                    new Dimension(700, 270)
            );
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            super.paintComponent(graphics);

            Graphics2D g =
                    (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int margenIzquierdo = 55;
            int margenDerecho = 20;
            int margenSuperior = 35;
            int margenInferior = 55;

            int ancho = getWidth()
                    - margenIzquierdo
                    - margenDerecho;

            int alto = getHeight()
                    - margenSuperior
                    - margenInferior;

            int maximo =
                    calcularMaximo();

            dibujarLineasGuia(
                    g,
                    margenIzquierdo,
                    margenSuperior,
                    ancho,
                    alto,
                    maximo
            );

            dibujarEjes(
                    g,
                    margenIzquierdo,
                    margenSuperior,
                    ancho,
                    alto
            );

            dibujarBarras(
                    g,
                    margenIzquierdo,
                    margenSuperior,
                    ancho,
                    alto,
                    maximo
            );

            g.dispose();
        }

        private int calcularMaximo() {
            int maximo = promedios.stream()
                    .filter(valor -> valor != null)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(10);

            int multiplo = 5;

            return Math.max(
                    10,
                    (
                        (maximo + multiplo - 1)
                        / multiplo
                    ) * multiplo
            );
        }

        private void dibujarLineasGuia(
                Graphics2D g,
                int margenIzquierdo,
                int margenSuperior,
                int ancho,
                int alto,
                int maximo
        ) {
            g.setFont(
                    Estilos.fuente(
                            Font.PLAIN,
                            10
                    )
            );

            for (int nivel = 0;
                    nivel <= 4;
                    nivel++) {

                int valor =
                        maximo * nivel / 4;

                int y = margenSuperior
                        + alto
                        - alto * nivel / 4;

                g.setColor(
                        new Color(
                                220,
                                225,
                                222
                        )
                );

                g.drawLine(
                        margenIzquierdo,
                        y,
                        margenIzquierdo + ancho,
                        y
                );

                g.setColor(
                        Estilos.TEXTO_SECUNDARIO
                );

                g.drawString(
                        valor + " min",
                        5,
                        y + 4
                );
            }
        }

        private void dibujarEjes(
                Graphics2D g,
                int margenIzquierdo,
                int margenSuperior,
                int ancho,
                int alto
        ) {
            int baseY =
                    margenSuperior + alto;

            g.setColor(
                    Estilos.TEXTO_SECUNDARIO
            );

            g.drawLine(
                    margenIzquierdo,
                    margenSuperior,
                    margenIzquierdo,
                    baseY
            );

            g.drawLine(
                    margenIzquierdo,
                    baseY,
                    margenIzquierdo + ancho,
                    baseY
            );
        }

        private void dibujarBarras(
                Graphics2D g,
                int margenIzquierdo,
                int margenSuperior,
                int ancho,
                int alto,
                int maximo
        ) {
            int cantidad =
                    INTERVALOS.length;

            int espacio =
                    ancho / cantidad;

            int anchoBarra =
                    Math.max(
                            12,
                            espacio - 14
                    );

            int baseY =
                    margenSuperior + alto;

            g.setFont(
                    Estilos.fuente(
                            Font.PLAIN,
                            10
                    )
            );

            for (int i = 0;
                    i < cantidad;
                    i++) {

                Integer valor =
                        obtenerValor(i);

                int x = margenIzquierdo
                        + i * espacio
                        + (
                            espacio - anchoBarra
                        ) / 2;

                dibujarIntervalo(
                        g,
                        INTERVALOS[i],
                        x,
                        anchoBarra,
                        baseY
                );

                if (valor == null) {
                    dibujarSinDatos(
                            g,
                            x,
                            anchoBarra,
                            baseY
                    );

                    continue;
                }

                int altoBarra =
                        valor * alto
                        / maximo;

                int y =
                        baseY - altoBarra;

                g.setColor(
                        Estilos.VERDE_MEDIO
                );

                g.fillRoundRect(
                        x,
                        y,
                        anchoBarra,
                        altoBarra,
                        8,
                        8
                );

                dibujarValor(
                        g,
                        valor,
                        x,
                        anchoBarra,
                        y
                );
            }
        }

        private Integer obtenerValor(
                int posicion
        ) {
            if (posicion >= promedios.size()) {
                return null;
            }

            return promedios.get(posicion);
        }

        private void dibujarValor(
                Graphics2D g,
                int valor,
                int x,
                int anchoBarra,
                int y
        ) {
            String texto =
                    String.valueOf(valor);

            FontMetrics metricas =
                    g.getFontMetrics();

            int textoX = x
                    + (
                        anchoBarra
                        - metricas.stringWidth(texto)
                    ) / 2;

            g.setColor(Estilos.TEXTO);

            g.drawString(
                    texto,
                    textoX,
                    y - 5
            );
        }

        private void dibujarIntervalo(
                Graphics2D g,
                String intervalo,
                int x,
                int anchoBarra,
                int baseY
        ) {
            FontMetrics metricas =
                    g.getFontMetrics();

            int textoX = x
                    + (
                        anchoBarra
                        - metricas.stringWidth(
                                intervalo
                        )
                    ) / 2;

            g.setColor(
                    Estilos.TEXTO_SECUNDARIO
            );

            g.drawString(
                    intervalo,
                    textoX,
                    baseY + 20
            );
        }

        private void dibujarSinDatos(
                Graphics2D g,
                int x,
                int anchoBarra,
                int baseY
        ) {
            String texto = "S/D";

            FontMetrics metricas =
                    g.getFontMetrics();

            int textoX = x
                    + (
                        anchoBarra
                        - metricas.stringWidth(texto)
                    ) / 2;

            g.setColor(
                    Estilos.TEXTO_SECUNDARIO
            );

            g.drawString(
                    texto,
                    textoX,
                    baseY - 8
            );
        }
    }
}