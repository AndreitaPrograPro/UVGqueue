package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

/** Dibujo decorativo de café, hojas y tiempo renderizado con Graphics2D. */
public class IlustracionCampus extends JPanel {
    public IlustracionCampus() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        int w = getWidth();
        int h = getHeight();
        g.setColor(new Color(255, 255, 255, 30));
        g.fillOval(w - 185, 12, 170, 170);
        g.fillOval(12, h - 120, 100, 100);
        g.setColor(new Color(255, 255, 255, 210));
        int cx = w / 2;
        int cy = h / 2 + 20;
        g.drawRoundRect(cx - 70, cy - 25, 112, 70, 18, 18);
        g.draw(new Arc2D.Double(cx + 24, cy - 10, 70, 48,
                -70, 150, Arc2D.OPEN));
        g.drawLine(cx - 90, cy + 58, cx + 90, cy + 58);
        Path2D vapor1 = new Path2D.Double();
        vapor1.moveTo(cx - 42, cy - 42);
        vapor1.curveTo(cx - 62, cy - 70, cx - 20, cy - 78,
                cx - 38, cy - 108);
        g.draw(vapor1);
        Path2D vapor2 = new Path2D.Double();
        vapor2.moveTo(cx, cy - 42);
        vapor2.curveTo(cx - 18, cy - 68, cx + 22, cy - 78,
                cx + 4, cy - 104);
        g.draw(vapor2);
        g.setColor(new Color(206, 247, 228, 230));
        for (int i = 0; i < 3; i++) {
            int x = 32 + i * 28;
            int y = 34 + i * 18;
            g.drawOval(x, y, 18, 32);
            g.drawLine(x + 9, y + 26, x + 24, y + 48);
        }
        g.dispose();
    }
}
