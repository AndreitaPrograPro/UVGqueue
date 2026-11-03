package view;

import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/** ScrollPane que desplaza de forma consistente en ambas direcciones. */
public class ScrollPaneSuave extends JScrollPane {
    public ScrollPaneSuave(JComponent contenido) {
        super(contenido);
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent evento) {
        if (!isWheelScrollingEnabled()) {
            super.processMouseWheelEvent(evento);
            return;
        }
        JScrollBar barra = getVerticalScrollBar();
        int unidades = evento.getUnitsToScroll();
        int destino = barra.getValue() + unidades * barra.getUnitIncrement();
        int maximo = Math.max(barra.getMinimum(),
                barra.getMaximum() - barra.getVisibleAmount());
        barra.setValue(Math.max(barra.getMinimum(), Math.min(maximo, destino)));
        evento.consume();
    }
}
