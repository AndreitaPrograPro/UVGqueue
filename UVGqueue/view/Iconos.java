package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/** Iconos vectoriales pequeños, independientes de emojis y archivos externos. */
public final class Iconos {
    private Iconos() {
    }

    public static Icon corazon(boolean relleno, Color color, int tamanio) {
        return new Icon() {
            public int getIconWidth() { return tamanio; }
            public int getIconHeight() { return tamanio; }
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g = preparar(graphics);
                Path2D p = new Path2D.Double();
                double s = tamanio / 24.0;
                p.moveTo(x + 12 * s, y + 21 * s);
                p.curveTo(x + 10 * s, y + 18 * s, x + 3 * s, y + 14 * s,
                        x + 3 * s, y + 8 * s);
                p.curveTo(x + 3 * s, y + 2 * s, x + 10 * s, y + 1 * s,
                        x + 12 * s, y + 6 * s);
                p.curveTo(x + 14 * s, y + 1 * s, x + 21 * s, y + 2 * s,
                        x + 21 * s, y + 8 * s);
                p.curveTo(x + 21 * s, y + 14 * s, x + 14 * s, y + 18 * s,
                        x + 12 * s, y + 21 * s);
                g.setColor(color);
                g.setStroke(new BasicStroke(Math.max(1.5f, tamanio / 11f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (relleno) g.fill(p); else g.draw(p);
                g.dispose();
            }
        };
    }

    public static Icon reloj(Color color, int tamanio) {
        return new Icon() {
            public int getIconWidth() { return tamanio; }
            public int getIconHeight() { return tamanio; }
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g = preparar(graphics);
                g.setColor(color);
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g.drawOval(x + 2, y + 2, tamanio - 5, tamanio - 5);
                g.drawLine(x + tamanio / 2, y + 5,
                        x + tamanio / 2, y + tamanio / 2);
                g.drawLine(x + tamanio / 2, y + tamanio / 2,
                        x + tamanio - 5, y + tamanio / 2 + 2);
                g.dispose();
            }
        };
    }

    public static Icon chispa(Color color, int tamanio) {
        return new Icon() {
            public int getIconWidth() { return tamanio; }
            public int getIconHeight() { return tamanio; }
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g = preparar(graphics);
                g.setColor(color);
                Path2D p = new Path2D.Double();
                p.moveTo(x + tamanio * .5, y);
                p.lineTo(x + tamanio * .63, y + tamanio * .37);
                p.lineTo(x + tamanio, y + tamanio * .5);
                p.lineTo(x + tamanio * .63, y + tamanio * .63);
                p.lineTo(x + tamanio * .5, y + tamanio);
                p.lineTo(x + tamanio * .37, y + tamanio * .63);
                p.lineTo(x, y + tamanio * .5);
                p.lineTo(x + tamanio * .37, y + tamanio * .37);
                p.closePath();
                g.fill(p);
                g.dispose();
            }
        };
    }

    private static Graphics2D preparar(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        return g;
    }
}
