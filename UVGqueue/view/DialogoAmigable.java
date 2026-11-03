package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Aviso compacto para reemplazar los JOptionPane informativos. */
public final class DialogoAmigable {
    private DialogoAmigable() {
    }

    public static void mostrar(Component padre, String titulo,
                               String mensaje, boolean error) {
        Window propietario = padre == null
                ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = propietario instanceof Frame frame
                ? new JDialog(frame, titulo, true)
                : propietario instanceof Dialog dialog
                        ? new JDialog(dialog, titulo, true)
                        : new JDialog((Frame) null, titulo, true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setUndecorated(true);

        PanelRedondeado tarjeta = new PanelRedondeado(26, Color.WHITE);
        tarjeta.setLayout(new BorderLayout(16, 0));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        tarjeta.setColorBorde(error
                ? new Color(245, 205, 205) : Estilos.VERDE_SUAVE);

        JLabel icono = new JLabel(error ? "!" : "✓");
        icono.setHorizontalAlignment(JLabel.CENTER);
        icono.setFont(Estilos.fuente(Font.BOLD, 17));
        icono.setForeground(Color.WHITE);
        icono.setOpaque(true);
        icono.setBackground(error ? Estilos.ERROR : Estilos.VERDE_MEDIO);
        icono.setPreferredSize(new Dimension(38, 38));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel encabezado = new JLabel(titulo);
        encabezado.setFont(Estilos.fuente(Font.BOLD, 14));
        encabezado.setForeground(Estilos.TEXTO);
        JLabel contenido = new JLabel("<html><div style='width:220px'>"
                + escaparHtml(mensaje) + "</div></html>");
        contenido.setFont(Estilos.fuente(Font.PLAIN, 11));
        contenido.setForeground(Estilos.TEXTO_SECUNDARIO);
        textos.add(encabezado);
        textos.add(Box.createVerticalStrut(5));
        textos.add(contenido);

        BotonAnimado aceptar = new BotonAnimado(
                "Entendido", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
        aceptar.setPreferredSize(new Dimension(92, 34));
        aceptar.addActionListener(evento -> dialogo.dispose());

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(aceptar, BorderLayout.EAST);
        tarjeta.add(icono, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(pie, BorderLayout.SOUTH);

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setOpaque(false);
        fondo.setBorder(BorderFactory.createEmptyBorder(7, 7, 9, 7));
        fondo.add(tarjeta);
        dialogo.setContentPane(fondo);
        dialogo.setSize(390, 175);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }
}
