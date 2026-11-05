package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.EstadoRestaurante;
import model.Reporte;
import model.Restaurante;

/** Ventana principal del estudiante con navegación, filtros y sugerencias. */
public class EstudianteFrame extends JFrame {
    private static final int ANCHO_LATERAL = 238;
    private static final int ANCHO_LATERAL_CONTRAIDO = 64;
    private final GridLayout rejilla = new GridLayout(0, 2, 18, 18);
    private final JPanel panelTarjetas = new JPanel(rejilla);
    private final JTextField campoBusqueda = new JTextField();
    private final JComboBox<String> filtroEstado = new JComboBox<>(
            new String[]{"Todos", "Abiertos", "Cerrados"});
    private final JLabel etiquetaSaludo = new JLabel("Hola, estudiante");
    private final JLabel etiquetaResumen = new JLabel("0 restaurantes");
    private final JButton botonActualizar = new BotonAnimado(
            "Actualizar", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
    private final BotonNavegacion botonInicio =
            new BotonNavegacion("⌂   Inicio", true);
    private final BotonNavegacion botonFavoritos =
            new BotonNavegacion("♡   Favoritos", false);
    private final BotonNavegacion botonReportes =
            new BotonNavegacion("≋   Mis reportes", false);
    private final JLabel etiquetaTitulo = new JLabel("¿Dónde quieres comer hoy?");
    private final JPanel contenidoQuick =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final PanelRedondeado panelQuick =
            new PanelRedondeado(24, Estilos.MENTA);
    private JPanel barraLateral;
    private JPanel contenidoLateral;
    private JLabel logoLateral;
    private JButton botonMenuLateral;
    private Timer animacionLateral;
    private int anchoLateralActual = ANCHO_LATERAL;
    private int anchoLateralObjetivo = ANCHO_LATERAL;

    private List<Restaurante> restaurantes = new ArrayList<>();
    private Map<String, Integer> tiemposPromedio = new HashMap<>();
    private Set<String> favoritos = new HashSet<>();
    private boolean soloFavoritos;
    private boolean mostrandoReportes;
    private Consumer<Restaurante> accionReportar;
    private Consumer<Restaurante> accionAlternarFavorito;
    private Consumer<Restaurante> accionVerDetalle;
    private BiConsumer<Restaurante, Boolean> accionVerificarCierre;
    private Runnable accionVerReportes;

    public EstudianteFrame() {
        this("estudiante");
    }

    public EstudianteFrame(String nombreUsuario) {
        Estilos.instalarLookAndFeel();
        etiquetaSaludo.setText("Qué gusto verte, " + nombreUsuario);
        configurarVentana();
        construirInterfaz();
        registrarEventos();
    }

    private void configurarVentana() {
        setTitle("FastQueue · Tu tiempo importa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 690));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Estilos.FONDO);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());
        barraLateral = crearBarraLateral();
        add(barraLateral, BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearBarraLateral() {
        JPanel lateral = new JPanel();
        lateral.setBackground(Estilos.VERDE_OSCURO);
        lateral.setPreferredSize(new Dimension(ANCHO_LATERAL, 0));
        lateral.setBorder(BorderFactory.createEmptyBorder(30, 22, 24, 22));
        lateral.setLayout(new BorderLayout(0, 4));

        logoLateral = new JLabel("FastQueue");
        logoLateral.setFont(Estilos.fuenteTitulo(30));
        logoLateral.setForeground(Color.WHITE);
        JLabel lema = crearEtiqueta("Tu tiempo importa",
                new Color(255, 241, 202), Font.PLAIN, 12);

        JPanel adorno = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        adorno.setOpaque(false);
        adorno.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel chispa = new JLabel(Iconos.chispa(
                Estilos.AZAFRAN, 13));
        adorno.add(chispa);
        adorno.add(lema);

        botonMenuLateral = new JButton(
                Estilos.cargarIcono("Menu.png", 25, 25, 0.92f));
        botonMenuLateral.setToolTipText("Contraer menú");
        botonMenuLateral.setPreferredSize(new Dimension(40, 40));
        botonMenuLateral.setMinimumSize(new Dimension(40, 40));
        botonMenuLateral.setMaximumSize(new Dimension(40, 40));
        botonMenuLateral.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        botonMenuLateral.setBackground(Color.WHITE);
        botonMenuLateral.setOpaque(true);
        botonMenuLateral.setBorderPainted(false);
        botonMenuLateral.setFocusPainted(false);
        botonMenuLateral.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonMenuLateral.addActionListener(evento -> {
            if (anchoLateralObjetivo == ANCHO_LATERAL) {
                contraerBarraLateral();
            } else {
                expandirBarraLateral();
            }
        });
        botonMenuLateral.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent evento) {
                if (anchoLateralObjetivo == ANCHO_LATERAL_CONTRAIDO) {
                    expandirBarraLateral();
                }
            }
        });

        JPanel filaLogo = new JPanel(new BorderLayout(6, 0));
        filaLogo.setOpaque(false);
        filaLogo.setPreferredSize(new Dimension(194, 42));
        filaLogo.add(logoLateral, BorderLayout.CENTER);
        filaLogo.add(botonMenuLateral, BorderLayout.EAST);

        contenidoLateral = new JPanel();
        contenidoLateral.setOpaque(false);
        contenidoLateral.setLayout(
                new BoxLayout(contenidoLateral, BoxLayout.Y_AXIS));
        contenidoLateral.add(adorno);
        contenidoLateral.add(Box.createVerticalStrut(42));
        contenidoLateral.add(botonInicio);
        contenidoLateral.add(Box.createVerticalStrut(9));
        contenidoLateral.add(botonFavoritos);
        contenidoLateral.add(Box.createVerticalStrut(9));
        contenidoLateral.add(botonReportes);
        contenidoLateral.add(Box.createVerticalGlue());

        IlustracionCampus dibujo = new IlustracionCampus();
        dibujo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
        dibujo.setPreferredSize(new Dimension(190, 155));
        dibujo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenidoLateral.add(dibujo);
        contenidoLateral.add(Box.createVerticalStrut(16));
        contenidoLateral.add(crearEtiqueta("Hecho para aprovechar tu tiempo",
                new Color(255, 231, 168), Font.PLAIN, 10));

        lateral.add(filaLogo, BorderLayout.NORTH);
        lateral.add(contenidoLateral, BorderLayout.CENTER);

        lateral.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent evento) {
                if (anchoLateralObjetivo == ANCHO_LATERAL_CONTRAIDO) {
                    expandirBarraLateral();
                }
            }
        });
        return lateral;
    }

    private void contraerBarraLateral() {
        mostrarContenidoLateral(false);
        anchoLateralObjetivo = ANCHO_LATERAL_CONTRAIDO;
        botonMenuLateral.setToolTipText("Desplegar menú");
        animarBarraLateral();
    }

    private void expandirBarraLateral() {
        anchoLateralObjetivo = ANCHO_LATERAL;
        botonMenuLateral.setToolTipText("Contraer menú");
        animarBarraLateral();
    }

    private void mostrarContenidoLateral(boolean visible) {
        logoLateral.setVisible(visible);
        contenidoLateral.setVisible(visible);
        barraLateral.setBorder(visible
                ? BorderFactory.createEmptyBorder(30, 22, 24, 22)
                : BorderFactory.createEmptyBorder(18, 8, 18, 8));
        barraLateral.revalidate();
        barraLateral.repaint();
    }

    private void animarBarraLateral() {
        if (animacionLateral != null && animacionLateral.isRunning()) {
            animacionLateral.stop();
        }
        animacionLateral = new Timer(12, evento -> {
            int diferencia = anchoLateralObjetivo - anchoLateralActual;
            if (Math.abs(diferencia) <= 3) {
                anchoLateralActual = anchoLateralObjetivo;
                ((Timer) evento.getSource()).stop();
                if (anchoLateralActual == ANCHO_LATERAL) {
                    mostrarContenidoLateral(true);
                }
            } else {
                anchoLateralActual += Math.max(3,
                        Math.round(Math.abs(diferencia) * 0.20f))
                        * Integer.signum(diferencia);
            }
            barraLateral.setPreferredSize(
                    new Dimension(anchoLateralActual, 0));
            barraLateral.revalidate();
            getContentPane().revalidate();
        });
        animacionLateral.start();
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 20));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(27, 32, 28, 32));
        contenido.add(crearEncabezado(), BorderLayout.NORTH);

        panelTarjetas.setBackground(Estilos.FONDO);
        JScrollPane scroll = new ScrollPaneSuave(panelTarjetas);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        Estilos.estilizarScroll(scroll);
        contenido.add(scroll, BorderLayout.CENTER);

        contenido.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int columnas = contenido.getWidth() < 840 ? 1 : 2;
                if (rejilla.getColumns() != columnas) {
                    rejilla.setColumns(columnas);
                    panelTarjetas.revalidate();
                }
            }
        });
        return contenido;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JPanel superior = new JPanel(new BorderLayout(15, 0));
        superior.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        etiquetaSaludo.setFont(Estilos.fuente(Font.PLAIN, 13));
        etiquetaSaludo.setForeground(Estilos.TEXTO_SECUNDARIO);
        etiquetaTitulo.setFont(Estilos.fuenteTitulo(31));
        etiquetaTitulo.setForeground(Estilos.TEXTO);
        titulos.add(etiquetaSaludo);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(etiquetaTitulo);

        etiquetaResumen.setFont(Estilos.fuente(Font.BOLD, 12));
        etiquetaResumen.setForeground(Estilos.VERDE_OSCURO);
        etiquetaResumen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.VERDE_SUAVE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        superior.add(titulos, BorderLayout.WEST);
        superior.add(etiquetaResumen, BorderLayout.EAST);

        JPanel herramientas = new JPanel(new BorderLayout(12, 0));
        herramientas.setOpaque(false);
        herramientas.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        campoBusqueda.setToolTipText("Buscar por nombre o ubicación");
        Estilos.estilizarCampo(campoBusqueda);
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 12));

        JPanel contenedorBusqueda = new JPanel(new BorderLayout(8, 0));
        contenedorBusqueda.setBackground(Color.WHITE);
        contenedorBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(0, 13, 0, 0)));
        JLabel lupa = new JLabel(
                Estilos.cargarIcono("lupa.jpg", 21, 21, 0.38f));
        lupa.setToolTipText("Buscar por nombre o ubicación");
        contenedorBusqueda.add(lupa, BorderLayout.WEST);
        contenedorBusqueda.add(campoBusqueda, BorderLayout.CENTER);
        filtroEstado.setPreferredSize(new Dimension(132, 42));
        filtroEstado.setBackground(Color.WHITE);
        filtroEstado.setForeground(Estilos.TEXTO);
        filtroEstado.setBorder(BorderFactory.createLineBorder(Estilos.BORDE));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        acciones.setOpaque(false);
        acciones.add(filtroEstado);
        acciones.add(botonActualizar);
        herramientas.add(contenedorBusqueda, BorderLayout.CENTER);
        herramientas.add(acciones, BorderLayout.EAST);

        configurarPanelQuick();
        encabezado.add(superior);
        encabezado.add(herramientas);
        encabezado.add(Box.createVerticalStrut(14));
        encabezado.add(panelQuick);
        return encabezado;
    }

    private void configurarPanelQuick() {
        panelQuick.setLayout(new BorderLayout(14, 0));
        panelQuick.setBorder(BorderFactory.createEmptyBorder(13, 16, 14, 16));
        panelQuick.setPreferredSize(new Dimension(0, 92));
        panelQuick.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        panelQuick.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelQuick.setColorBorde(Estilos.VERDE_SUAVE);

        JPanel titulo = new JPanel();
        titulo.setOpaque(false);
        titulo.setLayout(new BoxLayout(titulo, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel("Sugerencias Quick");
        nombre.setFont(Estilos.fuenteTitulo(22));
        nombre.setForeground(Estilos.VERDE_OSCURO);
        JLabel ayuda = new JLabel("Lo mejor para ahorrar tiempo ahora");
        ayuda.setFont(Estilos.fuente(Font.PLAIN, 10));
        ayuda.setForeground(Estilos.TEXTO_SECUNDARIO);
        titulo.add(nombre);
        titulo.add(ayuda);
        contenidoQuick.setOpaque(false);
        panelQuick.add(titulo, BorderLayout.WEST);
        panelQuick.add(contenidoQuick, BorderLayout.CENTER);
    }

    private void registrarEventos() {
        botonReportes.addActionListener(e -> {
            if (accionVerReportes != null) accionVerReportes.run();
        });
        filtroEstado.addActionListener(e -> aplicarFiltros());
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
    }

    public void mostrarRestaurantes(List<Restaurante> restaurantes) {
        this.restaurantes = new ArrayList<>(restaurantes);
        if (!mostrandoReportes) aplicarFiltros();
    }

    private void aplicarFiltros() {
        actualizarSugerenciasQuick();
        String texto = campoBusqueda.getText().trim().toLowerCase(Locale.ROOT);
        String filtro = (String) filtroEstado.getSelectedItem();
        List<Restaurante> visibles = new ArrayList<>();
        for (Restaurante restaurante : restaurantes) {
            boolean coincideTexto = restaurante.getNombre().toLowerCase(Locale.ROOT)
                    .contains(texto)
                    || restaurante.getUbicacion().toLowerCase(Locale.ROOT)
                    .contains(texto);
            boolean abierto = restaurante.getEstadoActual()
                    == EstadoRestaurante.ABIERTO;
            boolean coincideEstado = "Todos".equals(filtro)
                    || ("Abiertos".equals(filtro) && abierto)
                    || ("Cerrados".equals(filtro) && !abierto);
            boolean coincideFavorito = !soloFavoritos
                    || favoritos.contains(restaurante.getNombre());
            if (coincideTexto && coincideEstado && coincideFavorito) {
                visibles.add(restaurante);
            }
        }
        actualizarTarjetas(visibles);
    }

    private void actualizarSugerenciasQuick() {
        contenidoQuick.removeAll();
        Comparator<Restaurante> menorTiempo = Comparator
                .comparingInt((Restaurante r) ->
                        tiemposPromedio.get(r.getNombre()))
                .thenComparing(Restaurante::getNombre);
        Restaurante rapido = restaurantes.stream()
                .filter(this::puedeSugerirse)
                .min(menorTiempo).orElse(null);
        Restaurante favoritoRapido = restaurantes.stream()
                .filter(this::puedeSugerirse)
                .filter(r -> favoritos.contains(r.getNombre()))
                .min(menorTiempo).orElse(null);

        if (rapido == null) {
            JLabel vacio = new JLabel("Aún no hay tiempos recientes");
            vacio.setFont(Estilos.fuente(Font.PLAIN, 12));
            vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
            contenidoQuick.add(vacio);
        } else {
            contenidoQuick.add(crearChipQuick(rapido, "Menor espera"));
            if (favoritoRapido != null
                    && !favoritoRapido.getNombre().equals(rapido.getNombre())) {
                contenidoQuick.add(crearChipQuick(
                        favoritoRapido, "Tu favorito más rápido"));
            }
        }
        contenidoQuick.revalidate();
        contenidoQuick.repaint();
    }

    private boolean puedeSugerirse(Restaurante restaurante) {
        return restaurante.getEstadoActual() == EstadoRestaurante.ABIERTO
                && tiemposPromedio.containsKey(restaurante.getNombre());
    }

    private JPanel crearChipQuick(Restaurante restaurante, String motivo) {
        PanelRedondeado chip = new PanelRedondeado(18, Color.WHITE);
        chip.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 7));
        chip.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        JLabel icono = new JLabel(Iconos.reloj(Estilos.VERDE_MEDIO, 16));
        JLabel texto = new JLabel("<html><b>" + restaurante.getNombre()
                + " · " + tiemposPromedio.get(restaurante.getNombre())
                + " min</b><br><span style='font-size:9px'>" + motivo
                + "</span></html>");
        texto.setFont(Estilos.fuente(Font.PLAIN, 11));
        texto.setForeground(Estilos.TEXTO);
        chip.add(icono);
        chip.add(texto);
        return chip;
    }

    private void actualizarTarjetas(List<Restaurante> visibles) {
        panelTarjetas.removeAll();
        for (Restaurante restaurante : visibles) {
            panelTarjetas.add(crearTarjeta(restaurante));
        }
        if (visibles.isEmpty()) {
            panelTarjetas.add(crearEstadoVacio(soloFavoritos
                    ? "Aún no guardas restaurantes favoritos"
                    : "No encontramos restaurantes con esos filtros"));
        }
        long abiertos = restaurantes.stream()
                .filter(r -> r.getEstadoActual() == EstadoRestaurante.ABIERTO)
                .count();
        etiquetaResumen.setText(soloFavoritos
                ? visibles.size() + (visibles.size() == 1 ? " favorito" : " favoritos")
                : abiertos + " abiertos de " + restaurantes.size());
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearEstadoVacio(String texto) {
        PanelRedondeado panel = new PanelRedondeado(24, Color.WHITE);
        panel.setLayout(new BorderLayout());
        JLabel mensaje = new JLabel("✦  " + texto, SwingConstants.CENTER);
        mensaje.setFont(Estilos.fuenteTitulo(21));
        mensaje.setForeground(Estilos.TEXTO_SECUNDARIO);
        panel.add(mensaje, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjeta(Restaurante restaurante) {
        PanelImagen tarjeta = new PanelImagen(
                obtenerImagenRestaurante(restaurante.getNombre()));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(18, 19, 18, 19));
        tarjeta.setPreferredSize(new Dimension(410, 330));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean abierto = restaurante.getEstadoActual() == EstadoRestaurante.ABIERTO;
        boolean posible = restaurante.getEstadoActual()
                == EstadoRestaurante.POSIBLEMENTE_CERRADO;
        boolean enHorario = restaurante.estaEnHorario();
        boolean esFavorito = favoritos.contains(restaurante.getNombre());

        JPanel cabecera = new JPanel(new BorderLayout(8, 0));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JLabel estado = crearChipEstado(restaurante, abierto, posible, enHorario);
        BotonCorazon botonFavorito = new BotonCorazon(esFavorito);
        botonFavorito.addActionListener(e -> {
            if (accionAlternarFavorito != null) {
                accionAlternarFavorito.accept(restaurante);
            }
        });
        cabecera.add(estado, BorderLayout.WEST);
        cabecera.add(botonFavorito, BorderLayout.EAST);

        JLabel nombre = crearEtiqueta(restaurante.getNombre(),
                Estilos.TEXTO, Font.BOLD, 23);
        JLabel ubicacion = crearEtiqueta("⌖  " + restaurante.getUbicacion(),
                Estilos.TEXTO_SECUNDARIO, Font.PLAIN, 13);
        Integer promedio = tiemposPromedio.get(restaurante.getNombre());
        JLabel tiempo = new JLabel(promedio == null
                ? "Sin reportes recientes"
                : promedio + " min de espera promedio");
        tiempo.setIcon(Iconos.reloj(Estilos.VERDE_OSCURO, 17));
        tiempo.setIconTextGap(7);
        tiempo.setFont(Estilos.fuente(Font.BOLD, 13));
        tiempo.setForeground(Estilos.VERDE_OSCURO);
        tiempo.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotonAnimado botonReportar = new BotonAnimado(
                "Reportar fila", Estilos.VERDE_MEDIO, Estilos.VERDE_OSCURO);
        botonReportar.setEnabled(abierto);
        botonReportar.addActionListener(e -> {
            if (accionReportar != null) accionReportar.accept(restaurante);
        });
        BotonAnimado botonAbierto = new BotonAnimado(
                "Está abierto", new Color(74, 126, 105), Estilos.VERDE_OSCURO);
        BotonAnimado botonCerrado = new BotonAnimado(
                "Reportar cierre", new Color(169, 93, 91), Estilos.ERROR);
        botonAbierto.setEnabled(enHorario);
        botonCerrado.setEnabled(enHorario);
        botonAbierto.addActionListener(e -> {
            if (accionVerificarCierre != null)
                accionVerificarCierre.accept(restaurante, false);
        });
        botonCerrado.addActionListener(e -> {
            if (accionVerificarCierre != null)
                accionVerificarCierre.accept(restaurante, true);
        });

        JPanel acciones = new JPanel(new GridLayout(1, 3, 8, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        acciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
        acciones.add(botonReportar);
        acciones.add(botonAbierto);
        acciones.add(botonCerrado);

        tarjeta.add(cabecera);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(nombre);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(ubicacion);
        tarjeta.add(Box.createVerticalStrut(13));
        tarjeta.add(tiempo);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(acciones);
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (accionVerDetalle != null) accionVerDetalle.accept(restaurante);
            }
        });
        return tarjeta;
    }

    private JLabel crearChipEstado(Restaurante restaurante, boolean abierto,
                                   boolean posible, boolean enHorario) {
        String texto = abierto ? "● ABIERTO"
                : posible ? "● POSIBLEMENTE CERRADO"
                : enHorario ? "● CERRADO" : "● CERRADO POR HORARIO";
        Color color = abierto ? Estilos.VERDE_OSCURO
                : posible ? new Color(139, 91, 15) : Estilos.ERROR;
        Color fondo = abierto ? Estilos.VERDE_SUAVE
                : posible ? new Color(255, 241, 204) : new Color(255, 227, 227);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(fondo);
        etiqueta.setForeground(color);
        etiqueta.setFont(Estilos.fuente(Font.BOLD, 10));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        return etiqueta;
    }

    private JLabel crearEtiqueta(String texto, Color color, int estilo, int tamanio) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(color);
        etiqueta.setFont(Estilos.fuente(estilo, tamanio));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    public void mostrarFavoritos(Set<String> favoritos) {
        this.favoritos = new HashSet<>(favoritos);
        aplicarFiltros();
    }

    public void mostrarSoloFavoritos(boolean soloFavoritos) {
        this.mostrandoReportes = false;
        this.soloFavoritos = soloFavoritos;
        panelQuick.setVisible(true);
        etiquetaTitulo.setText(soloFavoritos
                ? "Tus lugares favoritos" : "¿Dónde quieres comer hoy?");
        seleccionarNavegacion(soloFavoritos ? 1 : 0);
        campoBusqueda.setEnabled(true);
        filtroEstado.setEnabled(true);
        aplicarFiltros();
    }

    private void seleccionarNavegacion(int indice) {
        botonInicio.setSeleccionado(indice == 0);
        botonFavoritos.setSeleccionado(indice == 1);
        botonReportes.setSeleccionado(indice == 2);
    }

    public void mostrarPromedios(Map<String, Integer> promedios) {
        tiemposPromedio = new HashMap<>(promedios);
        if (!mostrandoReportes) aplicarFiltros();
    }

    public void mostrarMisReportes(List<Reporte> reportes) {
        mostrandoReportes = true;
        panelQuick.setVisible(false);
        panelTarjetas.removeAll();
        etiquetaTitulo.setText("Tus reportes recientes");
        seleccionarNavegacion(2);
        campoBusqueda.setEnabled(false);
        filtroEstado.setEnabled(false);
        etiquetaResumen.setText(reportes.size() == 1
                ? "1 reporte" : reportes.size() + " reportes");
        if (reportes.isEmpty()) {
            panelTarjetas.add(crearEstadoVacio("Todavía no has realizado reportes"));
        } else {
            for (Reporte reporte : reportes) {
                panelTarjetas.add(crearTarjetaReporte(reporte));
            }
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearTarjetaReporte(Reporte reporte) {
        PanelRedondeado tarjeta = new PanelRedondeado(24, Color.WHITE);
        tarjeta.activarHover();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(21, 22, 25, 22));
        JLabel restaurante = new JLabel(reporte.getNombreRestaurante());
        restaurante.setFont(Estilos.fuenteTitulo(24));
        restaurante.setForeground(Estilos.TEXTO);
        JLabel personas = crearEtiqueta("♙  " + reporte.getCantidadPersonas()
                + " personas en fila", Estilos.TEXTO_SECUNDARIO,
                Font.PLAIN, 13);
        JLabel tiempo = crearEtiqueta("◷  " + reporte.getTiempoEspera()
                + " minutos de espera", Estilos.VERDE_OSCURO,
                Font.BOLD, 13);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm");
        JLabel fecha = crearEtiqueta(reporte.getFechaHora().format(formato),
                Estilos.TEXTO_SECUNDARIO, Font.PLAIN, 12);
        tarjeta.add(restaurante);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(personas);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(tiempo);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(fecha);
        return tarjeta;
    }

    private ImageIcon obtenerImagenRestaurante(String nombre) {
        String archivo = switch (nombre.toLowerCase(Locale.ROOT)) {
            case "&cafe" -> "&cafe.jfif";
            case "barista" -> "cafe_barista.jpg";
            case "gitane" -> "cafe_gitane.jpg";
            case "gogreen" -> "gogreen.jfif";
            case "sarita" -> "sarita.jfif";
            default -> "restaurante.png";
        };
        java.net.URL ruta = getClass().getResource("/images/" + archivo);
        if (ruta == null) ruta = getClass().getResource("/images/restaurante.png");
        return ruta == null ? null : new ImageIcon(ruta);
    }

    public JButton getBotonActualizar() { return botonActualizar; }
    public JButton getBotonInicio() { return botonInicio; }
    public JButton getBotonFavoritos() { return botonFavoritos; }
    public void setAccionReportar(Consumer<Restaurante> accion) {
        accionReportar = accion;
    }
    public void setAccionVerReportes(Runnable accion) { accionVerReportes = accion; }
    public void setAccionAlternarFavorito(Consumer<Restaurante> accion) {
        accionAlternarFavorito = accion;
    }
    public void setAccionVerificarCierre(BiConsumer<Restaurante, Boolean> accion) {
        accionVerificarCierre = accion;
    }
    public void setAccionVerDetalle(Consumer<Restaurante> accion) {
        accionVerDetalle = accion;
    }
    public String solicitarDato(String mensaje) {
        return JOptionPane.showInputDialog(this, mensaje);
    }
    public void mostrarMensaje(String titulo, String mensaje) {
        DialogoAmigable.mostrar(this, titulo, mensaje, false);
    }
    public void mostrarError(String mensaje) {
        DialogoAmigable.mostrar(this, "Algo salió mal", mensaje, true);
    }
}
