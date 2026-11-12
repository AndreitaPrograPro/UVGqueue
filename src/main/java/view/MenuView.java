package view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.ElementoMenu;
import model.MenuRestaurante;
import service.FiltroMenu;

public class MenuView extends VBox {
    private final TextField busqueda = new TextField();
    private final TextField minimo = new TextField();
    private final TextField maximo = new TextField();
    private final CheckBox baratos = new CheckBox("Más baratos primero");
    private final Button actualizar = new Button("↻  Actualizar menús");
    private final Label resumen = new Label();
    private final Label aviso = new Label();
    private final Label criterio = new Label();
    private final VBox resultados = new VBox(16);
    private final ScrollPane scroll = new ScrollPane(resultados);
    private final PauseTransition pausa = new PauseTransition(Duration.millis(180));
    private final DecimalFormat moneda = new DecimalFormat("'Q'#,##0.00",
            DecimalFormatSymbols.getInstance(Locale.US));
    private List<MenuRestaurante> menus = List.of();
    /** Restaurantes que la persona abrió con un clic (por id real de SQL). Sobrevive a recargas y filtros. */
    private final Set<Integer> abiertos = new HashSet<>();
    private boolean disponible;
    private Runnable accionActualizar;

    public MenuView() {
        super(14);
        getStyleClass().add("content");
        setPadding(new Insets(28, 34, 24, 34));
        Label titulo = new Label("Menú");
        titulo.getStyleClass().addAll("display", "h1");
        Label subtitulo = new Label("Encuentra qué comer en cada restaurante");
        subtitulo.getStyleClass().add("muted");
        HBox encabezado = new HBox(16, new VBox(3, titulo, subtitulo), espacio(), actualizar);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        actualizar.getStyleClass().add("primary-button");
        actualizar.setOnAction(event -> { if (accionActualizar != null) accionActualizar.run(); });

        busqueda.setId("menu-busqueda");
        busqueda.setPromptText("Buscar por nombre o tipo: bebida, pollo, desayuno…");
        busqueda.setAccessibleText("Buscar elementos del menú por nombre o tipo");
        busqueda.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(busqueda, Priority.ALWAYS);
        Button limpiar = new Button("Limpiar filtros");
        limpiar.getStyleClass().add("ghost-button");
        limpiar.setOnAction(event -> {
            busqueda.clear(); minimo.clear(); maximo.clear(); baratos.setSelected(true);
            pausa.stop(); aplicarFiltros();
        });
        HBox buscar = new HBox(12, busqueda, limpiar);
        buscar.setAlignment(Pos.CENTER_LEFT);
        configurarPrecio(minimo, "menu-precio-minimo", "Mínimo Q");
        configurarPrecio(maximo, "menu-precio-maximo", "Máximo Q");
        baratos.setId("menu-baratos");
        baratos.setSelected(true);
        FlowPane filtros = new FlowPane(14, 10, campoPrecio("Desde (Q)", minimo),
                campoPrecio("Hasta (Q)", maximo), baratos);
        filtros.setAlignment(Pos.CENTER_LEFT);
        aviso.getStyleClass().add("menu-error");
        aviso.setWrapText(true);
        aviso.managedProperty().bind(aviso.visibleProperty());
        aviso.setVisible(false);
        resumen.getStyleClass().add("menu-resumen");
        criterio.getStyleClass().add("muted");
        criterio.setWrapText(true);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resultados.setPadding(new Insets(2, 8, 12, 0));
        VBox.setVgrow(scroll, Priority.ALWAYS);
        getChildren().addAll(encabezado, buscar, filtros, aviso, resumen, criterio, scroll);
        pausa.setOnFinished(event -> aplicarFiltros());
        busqueda.textProperty().addListener((o, a, b) -> pausa.playFromStart());
        minimo.textProperty().addListener((o, a, b) -> pausa.playFromStart());
        maximo.textProperty().addListener((o, a, b) -> pausa.playFromStart());
        baratos.selectedProperty().addListener((o, a, b) -> aplicarFiltros());
    }

    private void configurarPrecio(TextField campo, String id, String prompt) {
        campo.setId(id);
        campo.setPromptText(prompt);
        campo.setAccessibleText(prompt);
        campo.setPrefWidth(125);
        campo.setMaxWidth(125);
    }

    private VBox campoPrecio(String texto, TextField campo) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("muted");
        return new VBox(4, etiqueta, campo);
    }

    public void setAccionActualizar(Runnable accion) { accionActualizar = accion; }

    public void mostrarCargando() {
        pausa.stop(); disponible = false; actualizar.setDisable(true);
        aviso.setVisible(false); resumen.setText("Cargando menús…"); criterio.setText("");
        ProgressIndicator progreso = new ProgressIndicator();
        progreso.setMaxSize(36, 36);
        resultados.getChildren().setAll(progreso);
    }

    public void mostrarMenus(List<MenuRestaurante> datos) {
        menus = List.copyOf(datos); disponible = true; actualizar.setDisable(false);
        aplicarFiltros();
    }

    public void mostrarError(String mensaje) {
        disponible = false; actualizar.setDisable(false);
        resumen.setText("Menús no disponibles"); criterio.setText("");
        resultados.getChildren().clear();
        aviso.setText(mensaje); aviso.setVisible(true);
    }

    private BigDecimal leerPrecio(TextField campo) {
        String texto = campo.getText().strip().replace(',', '.');
        if (texto.isEmpty()) return null;
        if (!texto.matches("\\d{1,6}(?:\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Escribe un precio desde 0, con hasta dos decimales, o deja el campo vacío.");
        }
        return new BigDecimal(texto);
    }

    private void aplicarFiltros() {
        if (!disponible) return;
        List<MenuRestaurante> visibles;
        boolean filtrando;
        try {
            BigDecimal desde = leerPrecio(minimo);
            BigDecimal hasta = leerPrecio(maximo);
            visibles = FiltroMenu.aplicar(menus, busqueda.getText(), desde, hasta, baratos.isSelected());
            filtrando = FiltroMenu.hayFiltros(busqueda.getText(), desde, hasta);
        } catch (IllegalArgumentException e) {
            resultados.getChildren().clear(); resumen.setText("Revisa el rango de precios");
            criterio.setText(""); aviso.setText(e.getMessage()); aviso.setVisible(true);
            return;
        }
        aviso.setVisible(false);
        String orden = baratos.isSelected()
                ? "Restaurantes ordenados por su producto más barato entre los resultados. Precios en quetzales."
                : "Restaurantes y productos en orden alfabético. Precios en quetzales.";
        criterio.setText(orden + (filtrando
                ? " Mientras haya una búsqueda o un rango de precio, los menús se muestran abiertos."
                : " Haz clic en un restaurante para ver su menú."));
        resultados.getChildren().clear();
        int cantidad = visibles.stream().mapToInt(m -> m.elementos().size()).sum();
        resumen.setText(cantidad + (cantidad == 1 ? " elemento" : " elementos") + " · "
                + visibles.size() + (visibles.size() == 1 ? " restaurante" : " restaurantes"));
        if (visibles.isEmpty()) {
            Label vacio = new Label(menus.isEmpty() ? "No hay restaurantes registrados."
                    : "No hay elementos que coincidan. Prueba otro nombre, tipo o precio.");
            vacio.getStyleClass().add("empty-state"); vacio.setWrapText(true);
            resultados.getChildren().add(vacio);
        } else {
            for (MenuRestaurante menu : visibles) resultados.getChildren().add(grupo(menu, filtrando));
        }
        scroll.setVvalue(0);
    }

    /**
     * Un restaurante: la cabecera es un botón que abre o cierra su menú. Sin filtros todos
     * empiezan cerrados; con búsqueda o rango de precio el menú queda abierto y fijo.
     */
    private VBox grupo(MenuRestaurante menu, boolean fijoAbierto) {
        Label nombre = new Label(menu.nombre());
        nombre.getStyleClass().add("menu-restaurante"); nombre.setWrapText(true);
        Label ubicacion = new Label(menu.ubicacion());
        ubicacion.getStyleClass().add("muted"); ubicacion.setWrapText(true);
        VBox identidad = new VBox(3, nombre, ubicacion);
        identidad.setMinWidth(0); HBox.setHgrow(identidad, Priority.ALWAYS);
        BigDecimal desde = FiltroMenu.precioMasBajo(menu);
        Label cantidad = new Label(menu.elementos().size()
                + (menu.elementos().size() == 1 ? " elemento" : " elementos")
                + (desde == null ? "" : " · Desde " + moneda.format(desde)));
        cantidad.getStyleClass().add("menu-resumen");
        cantidad.setMinWidth(Region.USE_PREF_SIZE);
        Region flecha = new Region();
        flecha.getStyleClass().add("menu-flecha");
        // Fija el espacio aunque se oculte, para que las tarjetas queden alineadas.
        flecha.setVisible(!fijoAbierto);
        HBox header = new HBox(12, identidad, cantidad, flecha);
        header.setAlignment(Pos.CENTER_LEFT);
        // El texto no recibe clics: un botón transparente encima cubre toda la cabecera.
        header.setMouseTransparent(true);
        Button boton = new Button();
        boton.getStyleClass().add("menu-grupo-boton");
        boton.setMaxWidth(Double.MAX_VALUE); boton.setMaxHeight(Double.MAX_VALUE);
        boton.setDisable(fijoAbierto);
        StackPane cabecera = new StackPane(header, boton);

        VBox contenido = new VBox(10);
        contenido.managedProperty().bind(contenido.visibleProperty());
        if (menu.elementos().isEmpty()) {
            Label vacio = new Label("Menú no disponible"); vacio.getStyleClass().add("muted");
            contenido.getChildren().add(vacio);
        } else {
            menu.elementos().forEach(elemento -> contenido.getChildren().add(fila(elemento)));
        }

        Runnable mostrar = () -> {
            boolean abierto = fijoAbierto || abiertos.contains(menu.idRestaurante());
            contenido.setVisible(abierto);
            flecha.setRotate(abierto ? 90 : 0);
            boton.setAccessibleText(menu.nombre() + ", menú " + (abierto ? "abierto" : "cerrado"));
            boton.setAccessibleHelp(fijoAbierto ? "Abierto mientras haya filtros"
                    : abierto ? "Pulsa para ocultar el menú" : "Pulsa para ver el menú");
        };
        boton.setOnAction(event -> {
            if (fijoAbierto) return;
            if (!abiertos.remove(menu.idRestaurante())) abiertos.add(menu.idRestaurante());
            mostrar.run();
        });
        mostrar.run();

        VBox caja = new VBox(10, cabecera, contenido);
        caja.getStyleClass().add("menu-grupo"); caja.setPadding(new Insets(18));
        return caja;
    }

    private HBox fila(ElementoMenu elemento) {
        Label nombre = new Label(elemento.nombre());
        nombre.getStyleClass().add("menu-producto"); nombre.setWrapText(true);
        FlowPane tipos = new FlowPane(7, 5);
        // Ambos tipos del Excel se mantienen, aunque coincidan entre sí.
        for (String tipo : List.of(elemento.tipo1(), elemento.tipo2())) {
            Label etiqueta = new Label(tipo); etiqueta.getStyleClass().add("menu-tipo");
            tipos.getChildren().add(etiqueta);
        }
        VBox texto = new VBox(5, nombre, tipos);
        texto.setMinWidth(0); HBox.setHgrow(texto, Priority.ALWAYS);
        Label precio = new Label(elemento.precio() == null
                ? "Precio no disponible" : moneda.format(elemento.precio()));
        precio.getStyleClass().add(elemento.precio() == null ? "muted" : "menu-precio");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        HBox fila = new HBox(16, texto, precio);
        fila.getStyleClass().add("menu-fila"); fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(11, 0, 11, 0));
        return fila;
    }

    private Region espacio() {
        Region region = new Region(); HBox.setHgrow(region, Priority.ALWAYS); return region;
    }
}
