package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.Timer;

/** Botón redondeado con transición de color al pasar el mouse. */
public class BotonAnimado extends JButton {
    private final Color colorBase;
    private final Color colorHover;
    private float progreso;
    private float objetivo;
    private final Timer animacion;
    private int radio = 18;

    public BotonAnimado(String texto, Color colorBase, Color colorHover) {
        super(texto);
        this.colorBase = colorBase;
        this.colorHover = colorHover;
        setForeground(Color.WHITE);
        setFont(Estilos.fuente(java.awt.Font.BOLD, 12));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        animacion = new Timer(16, evento -> animar());
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                objetivo = 1f;
                animacion.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                objetivo = 0f;
                animacion.start();
            }
        });
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    private void animar() {
        progreso += (objetivo - progreso) * 0.25f;
        if (Math.abs(objetivo - progreso) < 0.02f) {
            progreso = objetivo;
            animacion.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color actual = isEnabled()
                ? Estilos.mezclar(colorBase, colorHover, progreso)
                : new Color(190, 199, 195);
        int desplazamiento = getModel().isPressed() ? 2 : 0;
        g.setColor(actual);
        g.fillRoundRect(0, desplazamiento, getWidth(),
                getHeight() - desplazamiento, radio, radio);
        g.dispose();
        super.paintComponent(graphics);
    }
}
