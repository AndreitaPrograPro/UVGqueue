package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.EstadoRestaurante;
import model.Restaurante;

public class EstudianteFrame extends JFrame {

    private final JPanel panelTarjetas =
            new JPanel(new GridLayout(0, 2, 18, 18));

    private final JTextField campoBusqueda = new JTextField();

    private final JComboBox<String> filtroEstado =
            new JComboBox<>(new String[]{
                "Todos",
                "Abiertos",
                "Cerrados"
            });

    private final JLabel etiquetaSaludo =
            new JLabel("Hola, estudiante");

    private final JLabel etiquetaResumen =
            new JLabel("0 restaurantes");

    private final JButton botonActualizar =
            new JButton("Actualizar");

    private List<Restaurante> restaurantes =
            new ArrayList<>();

    public EstudianteFrame() {
        this("estudiante");
    }

    public EstudianteFrame(String nombreUsuario) {
        etiquetaSaludo.setText("Hola, " + nombreUsuario);
        configurarVentana();
        construirInterfaz();
        registrarEventos();
    }

    private void configurarVentana() {
        setTitle("UVGqueue - Restaurantes");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 650));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Estilos.FONDO);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());
        add(crearBarraLateral(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearBarraLateral() {
        JPanel lateral = new JPanel();
        lateral.setBackground(Estilos.VERDE_OSCURO);
        lateral.setPreferredSize(new Dimension(220, 0));
        lateral.setBorder(
                BorderFactory.createEmptyBorder(28, 20, 24, 20)
        );
        lateral.setLayout(
                new BoxLayout(lateral, BoxLayout.Y_AXIS)
        );

        JLabel logo = new JLabel("UVGqueue");
        logo.setForeground(Color.WHITE);
        logo.setFont(Estilos.fuente(Font.BOLD, 25));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lema = new JLabel("Tu tiempo importa");
        lema.setForeground(new Color(190, 222, 207));
        lema.setFont(Estilos.fuente(Font.PLAIN, 12));
        lema.setAlignmentX(Component.LEFT_ALIGNMENT);

        lateral.add(logo);
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(lema);
        lateral.add(Box.createVerticalStrut(48));
        lateral.add(crearBotonMenu("Inicio", true));
        lateral.add(Box.createVerticalStrut(10));
        lateral.add(crearBotonMenu("Favoritos", false));
        lateral.add(Box.createVerticalStrut(10));
        lateral.add(crearBotonMenu("Mis reportes", false));
        lateral.add(Box.createVerticalGlue());

        JLabel version = new JLabel("UVGqueue 2026");
        version.setForeground(new Color(151, 194, 174));
        version.setFont(Estilos.fuente(Font.PLAIN, 11));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(version);

        return lateral;
    }

    private JButton crearBotonMenu(
            String texto,
            boolean seleccionado
    ) {
        JButton boton = new JButton(texto);

        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        boton.setForeground(Color.WHITE);
        boton.setBackground(
                seleccionado
                        ? Estilos.VERDE_MEDIO
                        : Estilos.VERDE_OSCURO
        );
        boton.setBorder(
                BorderFactory.createEmptyBorder(0, 16, 0, 16)
        );
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

    private JPanel crearContenido() {
        JPanel contenido =
                new JPanel(new BorderLayout(0, 22));

        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(28, 34, 30, 34)
        );

        contenido.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        panelTarjetas.setBackground(Estilos.FONDO);

        JScrollPane scroll =
                new JScrollPane(panelTarjetas);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilos.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        contenido.add(scroll, BorderLayout.CENTER);

        return contenido;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(
                new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(
                new BoxLayout(titulos, BoxLayout.Y_AXIS)
        );

        etiquetaSaludo.setFont(
                Estilos.fuente(Font.PLAIN, 14)
        );
        etiquetaSaludo.setForeground(
                Estilos.TEXTO_SECUNDARIO
        );

        JLabel titulo =
                new JLabel("¿Dónde quieres comer hoy?");

        titulo.setFont(Estilos.fuente(Font.BOLD, 28));
        titulo.setForeground(Estilos.TEXTO);

        titulos.add(etiquetaSaludo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(titulo);

        etiquetaResumen.setForeground(
                Estilos.VERDE_OSCURO
        );

        superior.add(titulos, BorderLayout.WEST);
        superior.add(etiquetaResumen, BorderLayout.EAST);

        JPanel herramientas =
                new JPanel(new BorderLayout(12, 0));

        herramientas.setOpaque(false);
        herramientas.setBorder(
                BorderFactory.createEmptyBorder(22, 0, 0, 0)
        );

        campoBusqueda.setToolTipText(
                "Buscar por nombre o ubicación"
        );

        campoBusqueda.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Estilos.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                11, 14, 11, 14
                        )
                )
        );

        filtroEstado.setPreferredSize(
                new Dimension(125, 42)
        );

        Estilos.estilizarBotonPrincipal(
                botonActualizar
        );

        JPanel acciones =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                ));

        acciones.setOpaque(false);
        acciones.add(filtroEstado);
        acciones.add(botonActualizar);

        herramientas.add(
                campoBusqueda,
                BorderLayout.CENTER
        );
        herramientas.add(
                acciones,
                BorderLayout.EAST
        );

        encabezado.add(superior);
        encabezado.add(herramientas);

        return encabezado;
    }

    private void registrarEventos() {
        filtroEstado.addActionListener(
                evento -> aplicarFiltros()
        );

        campoBusqueda.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) {
                        aplicarFiltros();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        aplicarFiltros();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        aplicarFiltros();
                    }
                }
        );
    }

    public void mostrarRestaurantes(
            List<Restaurante> restaurantes
    ) {
        this.restaurantes =
                new ArrayList<>(restaurantes);

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = campoBusqueda.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String filtro =
                (String) filtroEstado.getSelectedItem();

        List<Restaurante> visibles =
                new ArrayList<>();

        for (Restaurante restaurante : restaurantes) {
            boolean coincideTexto =
                    restaurante.getNombre()
                            .toLowerCase(Locale.ROOT)
                            .contains(texto)
                    || restaurante.getUbicacion()
                            .toLowerCase(Locale.ROOT)
                            .contains(texto);

            boolean abierto =
                    restaurante.getEstado()
                    == EstadoRestaurante.ABIERTO;

            boolean coincideEstado =
                    "Todos".equals(filtro)
                    || ("Abiertos".equals(filtro) && abierto)
                    || ("Cerrados".equals(filtro) && !abierto);

            if (coincideTexto && coincideEstado) {
                visibles.add(restaurante);
            }
        }

        actualizarTarjetas(visibles);
    }

    private void actualizarTarjetas(
            List<Restaurante> visibles
    ) {
        panelTarjetas.removeAll();

        for (Restaurante restaurante : visibles) {
            panelTarjetas.add(
                    crearTarjeta(restaurante)
            );
        }

        if (visibles.isEmpty()) {
            JLabel mensaje = new JLabel(
                    "No se encontraron restaurantes.",
                    SwingConstants.CENTER
            );

            mensaje.setForeground(
                    Estilos.TEXTO_SECUNDARIO
            );

            panelTarjetas.add(mensaje);
        }

        long abiertos = restaurantes.stream()
                .filter(restaurante ->
                        restaurante.getEstado()
                        == EstadoRestaurante.ABIERTO)
                .count();

        etiquetaResumen.setText(
                abiertos + " abiertos de "
                + restaurantes.size()
        );

        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearTarjeta(
            Restaurante restaurante
    ) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(
                new BoxLayout(tarjeta, BoxLayout.Y_AXIS)
        );
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Estilos.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18, 18, 18, 18
                        )
                )
        );

        JLabel nombre =
                new JLabel(restaurante.getNombre());

        nombre.setFont(
                Estilos.fuente(Font.BOLD, 18)
        );
        nombre.setForeground(Estilos.TEXTO);

        JLabel ubicacion =
                new JLabel(restaurante.getUbicacion());

        ubicacion.setForeground(
                Estilos.TEXTO_SECUNDARIO
        );

        JLabel estado =
                new JLabel(restaurante.getEstado().toString());

        boolean abierto =
                restaurante.getEstado()
                == EstadoRestaurante.ABIERTO;

        estado.setForeground(
                abierto
                        ? Estilos.VERDE_MEDIO
                        : Estilos.ERROR
        );

        tarjeta.add(nombre);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(ubicacion);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(estado);

        return tarjeta;
    }

    public JButton getBotonActualizar() {
        return botonActualizar;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}