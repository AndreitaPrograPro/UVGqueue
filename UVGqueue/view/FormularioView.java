package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FormularioView extends JFrame {

    // Campos del formulario (Se agregó txtHoraActual aquí)
    private JTextField txtNombre;
    private JTextField txtCantidadEstudiantes;
    private JTextField txtTiempoAtencion;
    private JTextField txtHoraActual;

    public FormularioView() {
        setTitle("Registro de Fila - UVGqueue");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Ajustamos ligeramente el alto de la ventana para dar espacio al nuevo campo
        setSize(550, 720);
        setLocationRelativeTo(null);

        // Fondo general usando Estilos.FONDO
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(Estilos.FONDO);

        // Tarjeta estilo "Hoja de Documento"
        JPanel tarjetaDocumento = new JPanel();
        tarjetaDocumento.setLayout(new BoxLayout(tarjetaDocumento, BoxLayout.Y_AXIS));
        tarjetaDocumento.setBackground(Color.WHITE);
        tarjetaDocumento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE, 1),
                new EmptyBorder(30, 40, 30, 40)
        ));
        
        // Ajustamos la altura preferida de la tarjeta
        tarjetaDocumento.setPreferredSize(new Dimension(460, 620));

        // Encabezado
        JLabel lblTitulo = new JLabel("Reporte de Fila");
        lblTitulo.setFont(Estilos.fuente(Font.BOLD, 22));
        lblTitulo.setForeground(Estilos.VERDE_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Por favor, completa la información requerida.");
        lblSubtitulo.setFont(Estilos.fuente(Font.PLAIN, 13));
        lblSubtitulo.setForeground(Estilos.TEXTO_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setForeground(Estilos.BORDE);

        // Instanciación de los campos
        txtNombre = crearCampoTexto();
        txtCantidadEstudiantes = crearCampoTexto();
        txtTiempoAtencion = crearCampoTexto();
        txtHoraActual = crearCampoTexto();

        // Botón con estilo del proyecto
        JButton btnEnviar = new JButton("Enviar respuestas");
        Estilos.estilizarBotonPrincipal(btnEnviar);
        btnEnviar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnviar.addActionListener(this::validarYEnviar);

        // Ensamblar la vista
        tarjetaDocumento.add(lblTitulo);
        tarjetaDocumento.add(Box.createRigidArea(new Dimension(0, 5)));
        tarjetaDocumento.add(lblSubtitulo);
        tarjetaDocumento.add(Box.createRigidArea(new Dimension(0, 15)));
        tarjetaDocumento.add(separador);
        tarjetaDocumento.add(Box.createRigidArea(new Dimension(0, 20)));

        // Secciones de preguntas
        tarjetaDocumento.add(crearSeccionPregunta("Nombre completo:", txtNombre));
        tarjetaDocumento.add(crearSeccionPregunta("Cantidad de estudiantes que ves en la fila:", txtCantidadEstudiantes));
        tarjetaDocumento.add(crearSeccionPregunta("Tiempo que tardaste en ser atendido en minutos (Decimal):", txtTiempoAtencion));
        tarjetaDocumento.add(crearSeccionPregunta("Hora en la que comenzaste a hacer fila: (Solo la hora, sin minutos)", txtHoraActual));

        tarjetaDocumento.add(Box.createRigidArea(new Dimension(0, 15)));
        tarjetaDocumento.add(btnEnviar);

        panelFondo.add(tarjetaDocumento);
        add(panelFondo);
    }

    private JPanel crearSeccionPregunta(String etiqueta, JTextField campoTexto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilos.fuente(Font.BOLD, 13));
        lbl.setForeground(Estilos.TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(campoTexto);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));

        return panel;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setFont(Estilos.fuente(Font.PLAIN, 14));
        campo.setForeground(Estilos.TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    private void validarYEnviar(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        String estudiantesStr = txtCantidadEstudiantes.getText().trim();
        String tiempoStr = txtTiempoAtencion.getText().trim();
        String horaActual = txtHoraActual.getText().trim();

        // 1. Validar campos vacíos (Se incluye la hora)
        if (nombre.isEmpty() || estudiantesStr.isEmpty() || tiempoStr.isEmpty() || horaActual.isEmpty()) {
            mostrarError("Todos los campos son obligatorios. Por favor, completa la información.");
            return;
        }

        // 2. Validar que la cantidad de estudiantes sea entero (int)
        int cantidadEstudiantes;
        try {
            cantidadEstudiantes = Integer.parseInt(estudiantesStr);
            if (cantidadEstudiantes < 0) {
                mostrarError("La cantidad de estudiantes no puede ser un número negativo.");
                return;
            }
        } catch (NumberFormatException ex) {
            mostrarError("En 'Cantidad de estudiantes' debes ingresar un número entero válido.");
            return;
        }

        // 3. Validar que el tiempo sea un valor numérico decimal (double)
        double tiempoAtencion;
        try {
            tiempoAtencion = Double.parseDouble(tiempoStr);
            if (tiempoAtencion <= 0) {
                mostrarError("El tiempo de atención debe ser un número mayor a 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            mostrarError("En 'Tiempo de atención' debes ingresar un número válido (ej. 5 o 12.5).");
            return;
        }

        // 4. Procesar el envío si pasa la validación
        JOptionPane.showMessageDialog(
                this,
                "¡Registro enviado con éxito!\n\n" +
                "• Nombre: " + nombre + "\n" +
                "• Estudiantes en fila: " + cantidadEstudiantes + "\n" +
                "• Tiempo de atención: " + tiempoAtencion + " min\n" +
                "• Hora de inicio: " + horaActual,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
        );

        limpiarFormulario();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCantidadEstudiantes.setText("");
        txtTiempoAtencion.setText("");
        txtHoraActual.setText("");
    }
}