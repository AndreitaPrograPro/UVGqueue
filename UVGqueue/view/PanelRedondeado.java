package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Panel con esquinas suaves, borde y elevación animada. */
public class PanelRedondeado extends JPanel {
    private final int radio;
    private Color colorFondo;
    private Color colorBorde = Estilos.BORDE;
    private float elevacion;
    private float objetivoElevacion;
    private final Timer animacion;

    public PanelRedondeado(int radio, Color colorFondo) {
        this.radio = radio;
        this.colorFondo = colorFondo;
        setOpaque(false);
        animacion = new Timer(16, evento -> animar());
    }

    public void activarHover() {
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                objetivoElevacion = 1f;
                animacion.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                objetivoElevacion = 0f;
                animacion.start();
            }
        });
    }

    public void setColorFondo(Color color) {
        colorFondo = color;
        repaint();
    }

    public void setColorBorde(Color color) {
        colorBorde = color;
        repaint();
    }

    private void animar() {
        elevacion += (objetivoElevacion - elevacion) * 0.24f;
        if (Math.abs(objetivoElevacion - elevacion) < 0.02f) {
            elevacion = objetivoElevacion;
            animacion.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int sombra = Math.round(4 + elevacion * 5);
        g.setColor(new Color(16, 55, 43, 18 + Math.round(elevacion * 18)));
        g.fillRoundRect(3, sombra, getWidth() - 7,
                getHeight() - sombra - 2, radio, radio);
        g.setColor(colorFondo);
        g.fillRoundRect(1, 1, getWidth() - 4,
                getHeight() - sombra - 2, radio, radio);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Estilos.mezclar(colorBorde, Estilos.VERDE_MEDIO,
                elevacion * 0.55f));
        g.drawRoundRect(1, 1, getWidth() - 4,
                getHeight() - sombra - 2, radio, radio);
        g.dispose();
        super.paintComponent(graphics);
    }
}
