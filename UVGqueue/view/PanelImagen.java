package view;

import java.awt.*;
import javax.swing.*;

public class PanelImagen extends JPanel {

    private Image imagen;

    public PanelImagen(ImageIcon icono) {
        if (icono != null) {
            this.imagen = icono.getImage();
        }

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        // Bordes redondeados
        g2.setClip(
            new java.awt.geom.RoundRectangle2D.Double(
                0, 0, ancho, alto, 22, 22
            )
        );
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, ancho, alto);

        if (imagen != null) {
            int anchoImagen = imagen.getWidth(this);
            int altoImagen = imagen.getHeight(this);
            if (anchoImagen > 0 && altoImagen > 0) {
                double escala = Math.max(
                    (double) ancho / anchoImagen,
                    (double) alto / altoImagen
                );
                int nuevoAncho = (int) Math.ceil(anchoImagen * escala);
                int nuevoAlto = (int) Math.ceil(altoImagen * escala);
                g2.drawImage(
                    imagen,
                    (ancho - nuevoAncho) / 2,
                    (alto - nuevoAlto) / 2,
                    nuevoAncho,
                    nuevoAlto,
                    this
                );
            }
        }
        GradientPaint degradado = new GradientPaint(
            0, 0,
            new Color(255, 255, 255, 245),
            ancho, 0,
            new Color(255, 255, 255, 35)
        );

        g2.setPaint(degradado);
        g2.fillRect(0, 0, ancho, alto);

        g2.dispose();
    }
}