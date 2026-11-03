package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import model.EstadoRestaurante;
import model.Restaurante;

/** Detalle visual del restaurante y evolución de sus tiempos reportados. */
public class RestauranteDetalleFrame extends JFrame {
    public RestauranteDetalleFrame(Restaurante restaurante, Integer promedio,
                                   List<Integer> tiempos) {
        Estilos.instalarLookAndFeel();
        setTitle(restaurante.getNombre() + " · UVGqueue");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(860, 760);
        setMinimumSize(new Dimension(760, 680));
        setLocationRelativeTo(null);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(26, 31, 30, 31));
        contenido.add(crearEncabezado(restaurante, promedio));
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(crearEspacioMenu());
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(crearSeccionGrafica(tiempos));

        JScrollPane scroll = new ScrollPaneSuave(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilos.FONDO);
        Estilos.estilizarScroll(scroll);
        add(scroll);
    }

    private JPanel crearEncabezado(Restaurante restaurante, Integer promedio) {
        PanelRedondeado panel = new PanelRedondeado(30, Estilos.VERDE_OSCURO);
        panel.setLayout(new BorderLayout(25, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        panel.setPreferredSize(new Dimension(760, 190));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 27, 28));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel marca = etiqueta("UVGqueue recomienda", new Color(171, 228, 202),
                Font.BOLD, 11);
        JLabel nombre = new JLabel(restaurante.getNombre());
        nombre.setFont(Estilos.fuenteCaligrafica(35));
        nombre.setForeground(Color.WHITE);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ubicacion = etiqueta("⌖  " + restaurante.getUbicacion(),
                new Color(205, 232, 220), Font.PLAIN, 13);
        JLabel estado = etiqueta(obtenerTextoEstado(restaurante),
                Color.WHITE, Font.BOLD, 12);
        textos.add(marca);
        textos.add(Box.createVerticalStrut(3));
        textos.add(nombre);
        textos.add(Box.createVerticalStrut(6));
        textos.add(ubicacion);
        textos.add(Box.createVerticalStrut(12));
        textos.add(estado);

        PanelRedondeado tiempo = new PanelRedondeado(22,
                new Color(255, 255, 255, 28));
        tiempo.setLayout(new BoxLayout(tiempo, BoxLayout.Y_AXIS));
        tiempo.setPreferredSize(new Dimension(190, 120));
        tiempo.setBorder(BorderFactory.createEmptyBorder(17, 20, 18, 20));
        JLabel icono = new JLabel(Iconos.reloj(Color.WHITE, 22));
        icono.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel cifra = new JLabel(promedio == null ? "—" : promedio + " min");
        cifra.setFont(Estilos.fuente(Font.BOLD, 25));
        cifra.setForeground(Color.WHITE);
        cifra.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ayuda = etiqueta("promedio histórico",
                new Color(198, 231, 216), Font.PLAIN, 11);
        tiempo.add(icono);
        tiempo.add(Box.createVerticalStrut(7));
        tiempo.add(cifra);
        tiempo.add(ayuda);
        panel.add(textos, BorderLayout.CENTER);
        panel.add(tiempo, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearEspacioMenu() {
        PanelRedondeado panel = new PanelRedondeado(26, Color.WHITE);
        panel.setLayout(new BorderLayout(20, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        panel.setPreferredSize(new Dimension(760, 160));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 25, 24, 25));
        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Menú del restaurante");
        titulo.setFont(Estilos.fuenteCaligrafica(25));
        titulo.setForeground(Estilos.TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mensaje = etiqueta(
                "Este espacio está listo para recibir platillos y precios.",
                Estilos.TEXTO_SECUNDARIO, Font.PLAIN, 13);
        texto.add(titulo);
        texto.add(Box.createVerticalStrut(8));
        texto.add(mensaje);
        JLabel dibujo = new JLabel("☕  ·  ✦  ·  ♨", SwingConstants.CENTER);
        dibujo.setFont(Estilos.fuenteCaligrafica(31));
        dibujo.setForeground(Estilos.VERDE_MEDIO);
        panel.add(texto, BorderLayout.CENTER);
        panel.add(dibujo, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearSeccionGrafica(List<Integer> tiempos) {
        PanelRedondeado panel = new PanelRedondeado(26, Color.WHITE);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        panel.setPreferredSize(new Dimension(760, 330));
        panel.setBorder(BorderFactory.createEmptyBorder(21, 24, 24, 24));
        JLabel titulo = new JLabel("Promedio de espera por horario");
        titulo.setFont(Estilos.fuenteCaligrafica(25));
        titulo.setForeground(Estilos.TEXTO);
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new GraficaBarrasPanel(tiempos), BorderLayout.CENTER);
        return panel;
    }

    private String obtenerTextoEstado(Restaurante restaurante) {
        return switch (restaurante.getEstadoActual()) {
            case ABIERTO -> "●  ABIERTO AHORA";
            case POSIBLEMENTE_CERRADO -> "●  POSIBLEMENTE CERRADO";
            default -> restaurante.estaEnHorario()
                    ? "●  CERRADO" : "●  CERRADO POR HORARIO";
        };
    }

    private JLabel etiqueta(String texto, Color color, int estilo, int tamanio) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(color);
        etiqueta.setFont(Estilos.fuente(estilo, tamanio));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private static class GraficaBarrasPanel extends JPanel {
        private static final String[] INTERVALOS = {
            "07–08", "08–09", "09–10", "10–11", "11–12",
            "12–13", "13–14", "14–15", "15–16", "16–17",
            "17–18", "18–19", "19–20"
        };
        private final List<Integer> promedios;

        GraficaBarrasPanel(List<Integer> promedios) {
            this.promedios = promedios == null
                    ? new ArrayList<>() : new ArrayList<>(promedios);
            setOpaque(false);
            setPreferredSize(new Dimension(700, 240));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (promedios.isEmpty()) {
                g.setFont(Estilos.fuente(Font.PLAIN, 13));
                g.setColor(Estilos.TEXTO_SECUNDARIO);
                g.drawString("Aún no hay reportes suficientes para comparar horarios.",
                        24, getHeight() / 2);
                g.dispose();
                return;
            }

            int izquierda = 50, derecha = 16, arriba = 28, abajo = 44;
            int ancho = getWidth() - izquierda - derecha;
            int alto = getHeight() - arriba - abajo;
            int maximoReal = promedios.stream()
                    .filter(valor -> valor != null)
                    .mapToInt(Integer::intValue).max().orElse(10);
            int maximo = Math.max(10, ((maximoReal + 4) / 5) * 5);

            g.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int valor = maximo * (4 - i) / 4;
                int y = arriba + i * alto / 4;
                g.setColor(new Color(224, 232, 227));
                g.drawLine(izquierda, y, izquierda + ancho, y);
                g.setColor(Estilos.TEXTO_SECUNDARIO);
                g.setFont(Estilos.fuente(Font.PLAIN, 9));
                g.drawString(valor + "m", 10, y + 4);
            }

            int espacio = ancho / INTERVALOS.length;
            int anchoBarra = Math.max(10, espacio - 12);
            int base = arriba + alto;
            g.setFont(Estilos.fuente(Font.PLAIN, 8));
            FontMetrics metricas = g.getFontMetrics();

            for (int i = 0; i < INTERVALOS.length; i++) {
                int x = izquierda + i * espacio + (espacio - anchoBarra) / 2;
                Integer valor = i < promedios.size() ? promedios.get(i) : null;
                String intervalo = INTERVALOS[i];
                g.setColor(Estilos.TEXTO_SECUNDARIO);
                g.drawString(intervalo,
                        x + (anchoBarra - metricas.stringWidth(intervalo)) / 2,
                        base + 17);

                if (valor == null) {
                    g.setColor(new Color(201, 211, 206));
                    g.fillRoundRect(x, base - 4, anchoBarra, 4, 6, 6);
                    continue;
                }

                int altoBarra = Math.max(5, valor * alto / maximo);
                int y = base - altoBarra;
                g.setPaint(new GradientPaint(x, y, Estilos.VERDE_MEDIO,
                        x, base, Estilos.VERDE_OSCURO));
                g.fillRoundRect(x, y, anchoBarra, altoBarra, 8, 8);

                String texto = String.valueOf(valor);
                g.setColor(Estilos.TEXTO);
                g.drawString(texto,
                        x + (anchoBarra - metricas.stringWidth(texto)) / 2,
                        Math.max(arriba + 9, y - 5));
            }
            g.dispose();
        }
    }
}
