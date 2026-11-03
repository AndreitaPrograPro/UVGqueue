package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Scrollbar angosto y discreto para las vistas de UVGqueue. */
public class BarraDesplazamientoUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        trackColor = Estilos.FONDO;
        thumbColor = new Color(146, 190, 172);
    }

    @Override
    protected void paintTrack(Graphics graphics, JComponent componente,
                              Rectangle limites) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(new Color(230, 236, 232));
        g.fillRoundRect(limites.x + 3, limites.y,
                Math.max(3, limites.width - 6), limites.height, 8, 8);
        g.dispose();
    }

    @Override
    protected void paintThumb(Graphics graphics, JComponent componente,
                              Rectangle limites) {
        if (!componente.isEnabled() || limites.height <= 0) return;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isDragging ? Estilos.VERDE_MEDIO
                : new Color(118, 174, 151));
        g.fillRoundRect(limites.x + 3, limites.y + 2,
                Math.max(4, limites.width - 6),
                Math.max(8, limites.height - 4), 9, 9);
        g.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return crearBotonVacio();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return crearBotonVacio();
    }

    private JButton crearBotonVacio() {
        JButton boton = new JButton();
        boton.setPreferredSize(new Dimension(0, 0));
        boton.setMinimumSize(new Dimension(0, 0));
        boton.setMaximumSize(new Dimension(0, 0));
        return boton;
    }
}
