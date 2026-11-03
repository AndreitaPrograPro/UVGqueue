package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Formulario para registrar el estado de una fila. */
public class FormularioView extends JFrame {
    private final JTextField txtCantidadEstudiantes = new JTextField();
    private final JTextField txtTiempoAtencion = new JTextField();
    private BiConsumer<Integer, Double> accionEnviar;

    public FormularioView() {
        Estilos.instalarLookAndFeel();
        setTitle("UVGqueue · Reportar fila");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(570, 540);
        setResizable(false);
        setLocationRelativeTo(null);
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Estilos.FONDO);
        PanelRedondeado tarjeta = new PanelRedondeado(30, Color.WHITE);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(31, 38, 34, 38));
        tarjeta.setPreferredSize(new Dimension(485, 445));

        JLabel marca = new JLabel("UVGqueue");
        marca.setFont(Estilos.fuenteCaligrafica(25));
        marca.setForeground(Estilos.VERDE_MEDIO);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("¿Cómo está la fila?");
        titulo.setFont(Estilos.fuente(Font.BOLD, 25));
        titulo.setForeground(Estilos.TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = new JLabel("Tu reporte ayuda a toda la comunidad.");
        subtitulo.setFont(Estilos.fuente(Font.PLAIN, 13));
        subtitulo.setForeground(Estilos.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(marca);
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(25));
        tarjeta.add(crearSeccionPregunta(
                "Personas que viste en la fila", "Ejemplo: 8",
                txtCantidadEstudiantes));
        tarjeta.add(crearSeccionPregunta(
                "Minutos que esperaste", "Ejemplo: 12.5",
                txtTiempoAtencion));

        JButton btnEnviar = new BotonAnimado(
                "Compartir reporte", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
        btnEnviar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnviar.addActionListener(this::validarYEnviar);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(btnEnviar);
        fondo.add(tarjeta);
        add(fondo);
    }

    private JPanel crearSeccionPregunta(String etiqueta, String ayuda,
                                        JTextField campo) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 91));
        JLabel titulo = new JLabel(etiqueta);
        titulo.setFont(Estilos.fuente(Font.BOLD, 13));
        titulo.setForeground(Estilos.TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setToolTipText(ayuda);
        Estilos.estilizarCampo(campo);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(15));
        return panel;
    }

    private void validarYEnviar(ActionEvent evento) {
        String estudiantesTexto = txtCantidadEstudiantes.getText().trim();
        String tiempoTexto = txtTiempoAtencion.getText().trim();
        if (estudiantesTexto.isEmpty() || tiempoTexto.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        int cantidadEstudiantes;
        try {
            cantidadEstudiantes = Integer.parseInt(estudiantesTexto);
            if (cantidadEstudiantes < 0) {
                mostrarError("La cantidad de estudiantes no puede ser negativa.");
                return;
            }
        } catch (NumberFormatException excepcion) {
            mostrarError("La cantidad de estudiantes debe ser un número entero.");
            return;
        }
        double tiempoAtencion;
        try {
            tiempoAtencion = Double.parseDouble(tiempoTexto);
            if (tiempoAtencion <= 0) {
                mostrarError("El tiempo debe ser mayor a 0.");
                return;
            }
        } catch (NumberFormatException excepcion) {
            mostrarError("El tiempo debe ser un número válido, por ejemplo: 5 o 12.5.");
            return;
        }
        if (accionEnviar != null) {
            accionEnviar.accept(cantidadEstudiantes, tiempoAtencion);
        }
        limpiarFormulario();
    }

    private void mostrarError(String mensaje) {
        DialogoAmigable.mostrar(this, "Revisa los datos", mensaje, true);
    }

    private void limpiarFormulario() {
        txtCantidadEstudiantes.setText("");
        txtTiempoAtencion.setText("");
    }

    public void setAccionEnviar(BiConsumer<Integer, Double> accionEnviar) {
        this.accionEnviar = accionEnviar;
    }
}
