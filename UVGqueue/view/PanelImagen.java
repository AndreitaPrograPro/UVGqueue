package view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Tarjeta fotográfica con contraste y elevación suave al pasar el mouse. */
public class PanelImagen extends JPanel {
    private final Image imagen;
    private float hover;
    private float objetivo;
    private final Timer animacion;

    public PanelImagen(ImageIcon icono) {
        imagen = icono == null ? null : icono.getImage();
        setOpaque(false);
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

    private void animar() {
        hover += (objetivo - hover) * 0.22f;
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
        int sombra = 5 + Math.round(hover * 5);
        g.setColor(new Color(19, 56, 44, 20 + Math.round(hover * 22)));
        g.fillRoundRect(3, sombra, getWidth() - 7,
                getHeight() - sombra - 2, 28, 28);

        int altoTarjeta = getHeight() - sombra - 3;
        g.setClip(new RoundRectangle2D.Double(1, 1,
                getWidth() - 4, altoTarjeta, 28, 28));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), altoTarjeta);
        if (imagen != null) {
            int anchoImagen = imagen.getWidth(this);
            int altoImagen = imagen.getHeight(this);
            if (anchoImagen > 0 && altoImagen > 0) {
                double escala = Math.max(
                        (double) getWidth() / anchoImagen,
                        (double) altoTarjeta / altoImagen);
                int nuevoAncho = (int) Math.ceil(anchoImagen * escala);
                int nuevoAlto = (int) Math.ceil(altoImagen * escala);
                int extra = Math.round(hover * 8);
                g.drawImage(imagen,
                        (getWidth() - nuevoAncho - extra) / 2,
                        (altoTarjeta - nuevoAlto - extra) / 2,
                        nuevoAncho + extra, nuevoAlto + extra, this);
            }
        }
        GradientPaint capa = new GradientPaint(
                0, 0, new Color(255, 255, 255, 248),
                getWidth(), 0, new Color(255, 255, 255, 115));
        g.setPaint(capa);
        g.fillRect(0, 0, getWidth(), altoTarjeta);
        g.setClip(null);
        g.setColor(Estilos.mezclar(Estilos.BORDE, Estilos.VERDE_SUAVE,
                hover));
        g.drawRoundRect(1, 1, getWidth() - 4, altoTarjeta, 28, 28);
        g.dispose();
        super.paintComponent(graphics);
    }
}
