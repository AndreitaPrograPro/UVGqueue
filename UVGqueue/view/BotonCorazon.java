package view;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Timer;

/** Botón de favoritos con corazón blanco y pulso al presionarlo. */
public class BotonCorazon extends BotonAnimado {
    private final boolean favorito;
    private int tamanioIcono = 17;

    public BotonCorazon(boolean favorito) {
        super(favorito ? "En favoritos" : "Agregar a favoritos",
                Estilos.VERDE_OSCURO,
                Estilos.VERDE_MEDIO);
        this.favorito = favorito;
        setIcon(Iconos.corazon(favorito, Color.WHITE, tamanioIcono));
        setIconTextGap(5);
        setFont(Estilos.fuente(java.awt.Font.BOLD, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
        setPreferredSize(new Dimension(142, 34));
        setMaximumSize(new Dimension(142, 34));
        addActionListener(evento -> animarPulso());
    }

    private void animarPulso() {
        final int[] paso = {0};
        Timer pulso = new Timer(22, null);
        pulso.addActionListener(evento -> {
            paso[0]++;
            double onda = Math.sin(Math.min(1d, paso[0] / 12d) * Math.PI);
            int nuevo = 17 + (int) Math.round(onda * 5);
            if (nuevo != tamanioIcono) {
                tamanioIcono = nuevo;
                setIcon(Iconos.corazon(favorito, Color.WHITE, tamanioIcono));
            }
            if (paso[0] >= 12) {
                tamanioIcono = 17;
                setIcon(Iconos.corazon(favorito, Color.WHITE, tamanioIcono));
                pulso.stop();
            }
        });
        pulso.start();
    }
}
