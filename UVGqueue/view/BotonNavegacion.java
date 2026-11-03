package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.Timer;

/** Opción de la barra lateral con selección y hover animados. */
public class BotonNavegacion extends JButton {
    private boolean seleccionado;
    private float hover;
    private float objetivo;
    private final Timer animacion;

    public BotonNavegacion(String texto, boolean seleccionado) {
        super(texto);
        this.seleccionado = seleccionado;
        setHorizontalAlignment(LEFT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        setPreferredSize(new Dimension(190, 46));
        setForeground(Color.WHITE);
        setFont(Estilos.fuente(java.awt.Font.BOLD, 13));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 17, 0, 17));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        animacion = new Timer(16, e -> animar());
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

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    private void animar() {
        hover += (objetivo - hover) * 0.25f;
        if (Math.abs(objetivo - hover) < 0.02f) {
            hover = objetivo;
            animacion.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = seleccionado
                ? new Color(255, 255, 255, 35)
                : new Color(255, 255, 255, 0);
        Color encima = seleccionado
                ? new Color(255, 255, 255, 52)
                : new Color(255, 255, 255, 22);
        g.setColor(Estilos.mezclar(base, encima, hover));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 17, 17);
        if (seleccionado) {
            g.setColor(new Color(149, 239, 200));
            g.fillRoundRect(0, 9, 4, getHeight() - 18, 4, 4);
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
