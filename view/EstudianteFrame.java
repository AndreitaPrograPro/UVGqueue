package view;

import dao.FilaDAO;
import dao.RestauranteDAO;
import model.Fila;
import model.Restaurante;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Ventana principal para consultar los restaurantes disponibles. */
public class EstudianteFrame extends JFrame {

    private final RestauranteDAO restauranteDAO;
    private final FilaDAO filaDAO;
    private final JPanel panelTarjetas = new JPanel(new GridLayout(0, 2, 18, 18));
    private final JTextField campoBusqueda = new JTextField();
    private final JComboBox<String> filtroEstado =
            new JComboBox<>(new String[]{"Todos", "Abiertos", "Cerrados"});
    private final JLabel etiquetaResumen = new JLabel("Cargando...");
    private final JButton botonActualizar = new JButton("Actualizar");
    private List<RestauranteConFila> restaurantes = new ArrayList<>();

    public EstudianteFrame() {
        this(new RestauranteDAO(), new FilaDAO());
    }

    public EstudianteFrame(RestauranteDAO restauranteDAO, FilaDAO filaDAO) {
        this.restauranteDAO = restauranteDAO;
        this.filaDAO = filaDAO;
        configurarVentana();
        construirInterfaz();
        registrarEventos();
        cargarRestaurantes();
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
        lateral.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));

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
        lateral.add(crearBotonMenu("Mis turnos", false));
        lateral.add(Box.createVerticalGlue());

        JLabel version = new JLabel("UVGqueue 2026");
        version.setForeground(new Color(151, 194, 174));
        version.setFont(Estilos.fuente(Font.PLAIN, 11));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(version);
        return lateral;
    }

    private JButton crearBotonMenu(String texto, boolean seleccionado) {
        JButton boton = new JButton(texto);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setFont(Estilos.fuente(seleccionado ? Font.BOLD : Font.PLAIN, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(seleccionado ? Estilos.VERDE_MEDIO : Estilos.VERDE_OSCURO);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (!seleccionado) {
            boton.addActionListener(e -> Estilos.mostrarProximamente(this, texto));
        }
        return boton;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 22));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 34, 30, 34));
        contenido.add(crearEncabezado(), BorderLayout.NORTH);

        panelTarjetas.setBackground(Estilos.FONDO);
        JScrollPane scroll = new JScrollPane(panelTarjetas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilos.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contenido.add(scroll, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JPanel filaSuperior = new JPanel(new BorderLayout());
        filaSuperior.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel saludo = new JLabel("Hola, estudiante");
        saludo.setFont(Estilos.fuente(Font.PLAIN, 14));
        saludo.setForeground(Estilos.TEXTO_SECUNDARIO);
        JLabel titulo = new JLabel("¿Dónde quieres comer hoy?");
        titulo.setFont(Estilos.fuente(Font.BOLD, 28));
        titulo.setForeground(Estilos.TEXTO);
        titulos.add(saludo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(titulo);

        etiquetaResumen.setFont(Estilos.fuente(Font.BOLD, 13));
        etiquetaResumen.setForeground(Estilos.VERDE_OSCURO);
        etiquetaResumen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.VERDE_SUAVE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        filaSuperior.add(titulos, BorderLayout.WEST);
        filaSuperior.add(etiquetaResumen, BorderLayout.EAST);

        JPanel herramientas = new JPanel(new BorderLayout(12, 0));
        herramientas.setOpaque(false);
        herramientas.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        campoBusqueda.setFont(Estilos.fuente(Font.PLAIN, 14));
        campoBusqueda.setToolTipText("Buscar por nombre o ubicación");
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(11, 14, 11, 14)));
        filtroEstado.setPreferredSize(new Dimension(125, 42));
        filtroEstado.setBackground(Color.WHITE);
        Estilos.estilizarBotonPrincipal(botonActualizar);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(filtroEstado);
        acciones.add(botonActualizar);
        herramientas.add(campoBusqueda, BorderLayout.CENTER);
        herramientas.add(acciones, BorderLayout.EAST);

        encabezado.add(filaSuperior);
        encabezado.add(herramientas);
        return encabezado;
    }

    private void registrarEventos() {
        botonActualizar.addActionListener(e -> cargarRestaurantes());
        filtroEstado.addActionListener(e -> aplicarFiltros());
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
    }

    private void cargarRestaurantes() {
        mostrarMensaje("Consultando restaurantes...", Estilos.TEXTO_SECUNDARIO);
        botonActualizar.setEnabled(false);

        new SwingWorker<List<RestauranteConFila>, Void>() {
            protected List<RestauranteConFila> doInBackground() {
                List<RestauranteConFila> resultado = new ArrayList<>();
                for (Restaurante restaurante : restauranteDAO.obtenerTodos()) {
                    Fila fila = filaDAO.buscarPorRestaurante(restaurante.getIdRestaurante());
                    resultado.add(new RestauranteConFila(restaurante, fila));
                }
                return resultado;
            }

            protected void done() {
                try {
                    restaurantes = get();
                    aplicarFiltros();
                } catch (Exception e) {
                    mostrarMensaje("No se pudo cargar la información. Verifica MariaDB.", Estilos.ERROR);
                } finally {
                    botonActualizar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void aplicarFiltros() {
        String texto = campoBusqueda.getText().trim().toLowerCase(Locale.ROOT);
        String estado = (String) filtroEstado.getSelectedItem();
        List<RestauranteConFila> visibles = new ArrayList<>();

        for (RestauranteConFila elemento : restaurantes) {
            Restaurante r = elemento.restaurante;
            boolean coincideTexto = r.getNombre().toLowerCase(Locale.ROOT).contains(texto)
                    || r.getUbicacion().toLowerCase(Locale.ROOT).contains(texto);
            boolean coincideEstado = "Todos".equals(estado)
                    || ("Abiertos".equals(estado) && elemento.estaAbierto())
                    || ("Cerrados".equals(estado) && !elemento.estaAbierto());
            if (coincideTexto && coincideEstado) visibles.add(elemento);
        }
        mostrarRestaurantes(visibles);
    }

    private void mostrarRestaurantes(List<RestauranteConFila> visibles) {
        panelTarjetas.removeAll();
        panelTarjetas.setLayout(new GridLayout(0, 2, 18, 18));
        if (visibles.isEmpty()) {
            String mensaje = restaurantes.isEmpty()
                    ? "No hay restaurantes. Revisa MariaDB o registra uno."
                    : "No encontramos restaurantes con esos filtros.";
            mostrarMensaje(mensaje, Estilos.TEXTO_SECUNDARIO);
        } else {
            for (RestauranteConFila elemento : visibles) {
                panelTarjetas.add(new RestaurantePanel(elemento.restaurante, elemento.fila));
            }
        }
        long abiertos = restaurantes.stream().filter(RestauranteConFila::estaAbierto).count();
        etiquetaResumen.setText(abiertos + " abiertos de " + restaurantes.size());
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private void mostrarMensaje(String mensaje, Color color) {
        panelTarjetas.removeAll();
        JLabel etiqueta = new JLabel(mensaje, SwingConstants.CENTER);
        etiqueta.setFont(Estilos.fuente(Font.PLAIN, 15));
        etiqueta.setForeground(color);
        panelTarjetas.add(etiqueta);
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private static final class RestauranteConFila {
        private final Restaurante restaurante;
        private final Fila fila;

        private RestauranteConFila(Restaurante restaurante, Fila fila) {
            this.restaurante = restaurante;
            this.fila = fila;
        }

        private boolean estaAbierto() {
            return restaurante.isActivo() && fila != null
                    && "ABIERTA".equalsIgnoreCase(fila.getEstado());
        }
    }
}
