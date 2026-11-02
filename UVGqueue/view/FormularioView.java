package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FormularioView extends JFrame {
    private JTextField txtCantidadEstudiantes;
    private JTextField txtTiempoAtencion;
    private BiConsumer<Integer, Double> accionEnviar;
    public FormularioView() {
        setTitle("Registro de Fila - UVGqueue");
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        setSize(550, 520);
        setLocationRelativeTo(null);
        JPanel panelFondo =
                new JPanel(new GridBagLayout());
        panelFondo.setBackground(
                Estilos.FONDO
        );
        JPanel tarjetaDocumento =
                new JPanel();
        tarjetaDocumento.setLayout(
                new BoxLayout(
                        tarjetaDocumento,
                        BoxLayout.Y_AXIS
                )
        );
        tarjetaDocumento.setBackground(
                Color.WHITE
        );
        tarjetaDocumento.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Estilos.BORDE,
                                1
                        ),
                        new EmptyBorder(
                                30,
                                40,
                                30,
                                40
                        )
                )
        );
        tarjetaDocumento.setPreferredSize(
                new Dimension(460, 410)
        );
        JLabel lblTitulo =
                new JLabel("Reporte de Fila");
        lblTitulo.setFont(
                Estilos.fuente(
                        Font.BOLD,
                        22
                )
        );
        lblTitulo.setForeground(
                Estilos.VERDE_OSCURO
        );
        lblTitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        JLabel lblSubtitulo = new JLabel(
                "Comparte cuánto tiempo tardaste."
        );
        lblSubtitulo.setFont(
                Estilos.fuente(
                        Font.PLAIN,
                        13
                )
        );
        lblSubtitulo.setForeground(
                Estilos.TEXTO_SECUNDARIO
        );
        lblSubtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
        JSeparator separador =
                new JSeparator();
        separador.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );
        separador.setForeground(Estilos.BORDE);
        txtCantidadEstudiantes =crearCampoTexto();

        txtTiempoAtencion =
                crearCampoTexto();

        JButton btnEnviar =
                new JButton("Enviar reporte");

        Estilos.estilizarBotonPrincipal(
                btnEnviar
        );

        btnEnviar.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        btnEnviar.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        btnEnviar.addActionListener(
                this::validarYEnviar
        );

        tarjetaDocumento.add(lblTitulo);

        tarjetaDocumento.add(
                Box.createRigidArea(
                        new Dimension(0, 5)
                )
        );

        tarjetaDocumento.add(lblSubtitulo);

        tarjetaDocumento.add(
                Box.createRigidArea(
                        new Dimension(0, 15)
                )
        );

        tarjetaDocumento.add(separador);

        tarjetaDocumento.add(
                Box.createRigidArea(
                        new Dimension(0, 20)
                )
        );

        tarjetaDocumento.add(
                crearSeccionPregunta(
                        "Cantidad de estudiantes en la fila:",
                        txtCantidadEstudiantes
                )
        );

        tarjetaDocumento.add(
                crearSeccionPregunta(
                        "¿Cuántos minutos tardaste en ser atendido?",
                        txtTiempoAtencion
                )
        );

        tarjetaDocumento.add(
                Box.createRigidArea(
                        new Dimension(0, 15)
                )
        );

        tarjetaDocumento.add(btnEnviar);

        panelFondo.add(tarjetaDocumento);
        add(panelFondo);
    }

    private JPanel crearSeccionPregunta(
            String etiqueta,
            JTextField campoTexto
    ) {
        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(Color.WHITE);

        panel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel lbl =
                new JLabel(etiqueta);

        lbl.setFont(
                Estilos.fuente(
                        Font.BOLD,
                        13
                )
        );

        lbl.setForeground(
                Estilos.TEXTO
        );

        lbl.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(lbl);

        panel.add(
                Box.createRigidArea(
                        new Dimension(0, 6)
                )
        );

        panel.add(campoTexto);

        panel.add(
                Box.createRigidArea(
                        new Dimension(0, 18)
                )
        );

        return panel;
    }

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        campo.setFont(
                Estilos.fuente(
                        Font.PLAIN,
                        14
                )
        );

        campo.setForeground(
                Estilos.TEXTO
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Estilos.BORDE,
                                1
                        ),
                        new EmptyBorder(
                                5,
                                10,
                                5,
                                10
                        )
                )
        );

        campo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return campo;
    }

    private void validarYEnviar(
            ActionEvent evento
    ) {
        String estudiantesTexto =
                txtCantidadEstudiantes
                        .getText()
                        .trim();

        String tiempoTexto =
                txtTiempoAtencion
                        .getText()
                        .trim();

        if (estudiantesTexto.isEmpty()
                || tiempoTexto.isEmpty()) {

            mostrarError(
                    "Todos los campos son obligatorios."
            );

            return;
        }

        int cantidadEstudiantes;

        try {
            cantidadEstudiantes =
                    Integer.parseInt(
                            estudiantesTexto
                    );

            if (cantidadEstudiantes < 0) {
                mostrarError(
                        "La cantidad de estudiantes "
                        + "no puede ser negativa."
                );

                return;
            }

        } catch (NumberFormatException excepcion) {

            mostrarError(
                    "La cantidad de estudiantes "
                    + "debe ser un número entero."
            );

            return;
        }

        double tiempoAtencion;

        try {
            tiempoAtencion =
                    Double.parseDouble(
                            tiempoTexto
                    );

            if (tiempoAtencion <= 0) {
                mostrarError(
                        "El tiempo debe ser mayor a 0."
                );

                return;
            }

        } catch (NumberFormatException excepcion) {

            mostrarError(
                    "El tiempo debe ser un número válido, "
                    + "por ejemplo: 5 o 12.5."
            );

            return;
        }

        if (accionEnviar != null) {

            accionEnviar.accept(
                    cantidadEstudiantes,
                    tiempoAtencion
            );
        }

        limpiarFormulario();
    }

    private void mostrarError(
            String mensaje
    ) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error de validación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void limpiarFormulario() {

        txtCantidadEstudiantes.setText("");
        txtTiempoAtencion.setText("");
    }

    public void setAccionEnviar(
            BiConsumer<Integer, Double> accionEnviar
    ) {
        this.accionEnviar = accionEnviar;
    }
}