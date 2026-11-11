package view;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.EstadoRestaurante;
import model.Reporte;
import model.Restaurante;

public class EstudianteFrame {
    private final Stage stage = new Stage();
    private final BorderPane root = new BorderPane();
    private final VBox sidebar = new VBox(12);
    private final VBox sidebarText = new VBox(4);
    private final Button botonInicio = nav("⌂  Inicio");
    private final Button botonFavoritos = nav("♡  Favoritos");
    private final Button botonReportes = nav("▤  Mis reportes");
    private final Button botonActualizar = new Button("↻  Actualizar");
    private final TextField campoBusqueda = new TextField();
    private final ComboBox<String> filtroEstado = new ComboBox<>();
    private final Label titulo = new Label("¿Dónde quieres comer hoy?");
    private final Label resumen = new Label("Cargando…");
    private final HBox quickContent = new HBox(12);
    private final TilePane tarjetas = new TilePane();
    private final ScrollPane scroll = new ScrollPane(tarjetas);
    private final StackPane body = new StackPane();
    private final String nombreUsuario;

    private List<Restaurante> restaurantes = new ArrayList<>();
    private Map<String, Integer> tiemposPromedio = new HashMap<>();
    private Set<String> favoritos = new HashSet<>();
    private boolean soloFavoritos;
    private boolean mostrandoReportes;
    private boolean menuExpandido = true;
    private Consumer<Restaurante> accionReportar;
    private Consumer<Restaurante> accionAlternarFavorito;
    private Consumer<Restaurante> accionVerDetalle;
    private BiConsumer<Restaurante, Boolean> accionVerificarCierre;
    private Runnable accionVerReportes;

    public EstudianteFrame(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        construir();
        registrarEventos();
    }

    public EstudianteFrame() { this("estudiante"); }

    private void construir() {
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(270);
        sidebar.setMinWidth(72);
        sidebar.setPadding(new Insets(22, 14, 22, 14));
        root.setLeft(sidebar);
        root.setCenter(contenido());

        Button menu = new Button("☰");
        menu.getStyleClass().add("menu-button");
        menu.setMinSize(42, 42);
        menu.setOnAction(e -> alternarMenu());

        Label brand = new Label("FastQueue");
        brand.getStyleClass().add("sidebar-brand");
        Label brandSub = new Label("✦ Tu tiempo importa");
        brandSub.getStyleClass().add("sidebar-subtitle");
        sidebarText.getChildren().addAll(brand, brandSub);
        HBox top = new HBox(12, menu, sidebarText);
        top.setAlignment(Pos.CENTER_LEFT);

        botonInicio.getStyleClass().add("active");
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        Label footer = new Label("Hecho para aprovechar tu tiempo");
        footer.getStyleClass().add("sidebar-subtitle");
        footer.setWrapText(true);
        sidebar.getChildren().addAll(top, space(55), botonInicio, botonFavoritos, botonReportes, push, footer);
        sidebar.setOnMouseEntered(e -> { if (!menuExpandido) expandirMenu(true); });

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("FastQueue · Tu tiempo importa");
        stage.setMinWidth(980);
        stage.setMinHeight(650);
        stage.centerOnScreen();
    }

    private Node contenido() {
        VBox content = new VBox(18);
        content.getStyleClass().add("content");
        content.setPadding(new Insets(28, 34, 24, 34));

        Label hello = new Label("Qué gusto verte, " + nombreUsuario);
        hello.getStyleClass().add("muted");
        titulo.getStyleClass().addAll("display", "h1");
        VBox titles = new VBox(3, hello, titulo);
        HBox header = new HBox(titles, spacer(), resumen);
        header.setAlignment(Pos.TOP_LEFT);
        resumen.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-padding:12 16;-fx-font-weight:bold;-fx-text-fill:#4F7A52;");

        Label glass = new Label("⌕");
        glass.setStyle("-fx-font-size:26px;-fx-text-fill:rgba(59,42,32,.45);");
        campoBusqueda.setPromptText("Buscar por nombre o ubicación");
        HBox search = new HBox(8, glass, campoBusqueda);
        search.getStyleClass().add("search-box");
        search.setAlignment(Pos.CENTER_LEFT);
        search.setPadding(new Insets(0, 12, 0, 12));
        HBox.setHgrow(campoBusqueda, Priority.ALWAYS);
        campoBusqueda.setMaxWidth(Double.MAX_VALUE);

        filtroEstado.setItems(FXCollections.observableArrayList("Todos", "Abiertos", "Cerrados"));
        filtroEstado.setValue("Todos");
        filtroEstado.setPrefWidth(145);
        botonActualizar.getStyleClass().add("primary-button");
        HBox tools = new HBox(12, search, filtroEstado, botonActualizar);
        HBox.setHgrow(search, Priority.ALWAYS);
        tools.setAlignment(Pos.CENTER);

        VBox quickCopy = new VBox(2);
        Label quickTitle = new Label("Sugerencias Quick");
        quickTitle.getStyleClass().addAll("display", "h2");
        Label quickSub = new Label("Lo mejor para ahorrar tiempo ahora");
        quickSub.getStyleClass().add("muted");
        quickCopy.getChildren().addAll(quickTitle, quickSub);
        HBox quick = new HBox(30, quickCopy, quickContent);
        quick.getStyleClass().add("quick-panel");
        quick.setPadding(new Insets(18, 22, 18, 22));
        quick.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(quickContent, Priority.ALWAYS);

        tarjetas.setHgap(18);
        tarjetas.setVgap(18);
        tarjetas.setPrefColumns(2);
        tarjetas.setTileAlignment(Pos.TOP_LEFT);
        tarjetas.setPadding(new Insets(4, 5, 20, 3));
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        body.getChildren().add(scroll);
        VBox.setVgrow(body, Priority.ALWAYS);
        content.getChildren().addAll(header, tools, quick, body);
        return content;
    }

    private void registrarEventos() {
        campoBusqueda.textProperty().addListener((o, a, b) -> aplicarFiltros());
        filtroEstado.valueProperty().addListener((o, a, b) -> aplicarFiltros());
        botonInicio.setOnAction(e -> mostrarSoloFavoritos(false));
        botonFavoritos.setOnAction(e -> mostrarSoloFavoritos(true));
        botonReportes.setOnAction(e -> { if (accionVerReportes != null) accionVerReportes.run(); });
    }

    private Button nav(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-button");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMinHeight(48);
        return b;
    }

    private void alternarMenu() { expandirMenu(!menuExpandido); }

    private void expandirMenu(boolean expandir) {
        menuExpandido = expandir;
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(230),
                new KeyValue(sidebar.prefWidthProperty(), expandir ? 270 : 72, Interpolator.EASE_BOTH)));
        if (expandir) {
            sidebarText.setVisible(true);
            botonInicio.setText("⌂  Inicio");
            botonFavoritos.setText("♡  Favoritos");
            botonReportes.setText("▤  Mis reportes");
        } else {
            sidebarText.setVisible(false);
            botonInicio.setText("⌂");
            botonFavoritos.setText("♡");
            botonReportes.setText("▤");
        }
        timeline.play();
    }

    public void mostrarRestaurantes(List<Restaurante> restaurantes) {
        this.restaurantes = new ArrayList<>(restaurantes);
        if (!mostrandoReportes) aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (mostrandoReportes) return;
        String q = campoBusqueda.getText() == null ? "" : campoBusqueda.getText().trim().toLowerCase(Locale.ROOT);
        String estado = filtroEstado.getValue();
        List<Restaurante> visibles = restaurantes.stream().filter(r -> {
            boolean texto = r.getNombre().toLowerCase(Locale.ROOT).contains(q)
                    || r.getUbicacion().toLowerCase(Locale.ROOT).contains(q);
            boolean abierto = r.getEstadoActual() == EstadoRestaurante.ABIERTO;
            boolean filtro = "Todos".equals(estado) || ("Abiertos".equals(estado) && abierto)
                    || ("Cerrados".equals(estado) && !abierto);
            return texto && filtro && (!soloFavoritos || favoritos.contains(r.getNombre()));
        }).toList();
        actualizarTarjetas(visibles);
    }

    private void actualizarTarjetas(List<Restaurante> visibles) {
        tarjetas.getChildren().clear();
        if (visibles.isEmpty()) {
            Label empty = new Label(soloFavoritos ? "Todavía no tienes favoritos" : "No encontramos restaurantes");
            empty.getStyleClass().add("empty-state");
            tarjetas.getChildren().add(empty);
        } else {
            visibles.forEach(r -> tarjetas.getChildren().add(crearTarjeta(r)));
        }
        long abiertos = restaurantes.stream().filter(r -> r.getEstadoActual() == EstadoRestaurante.ABIERTO).count();
        resumen.setText(soloFavoritos ? visibles.size() + " favoritos" : abiertos + " abiertos de " + restaurantes.size());
        actualizarQuick();
        animarEntrada();
    }

    private Pane crearTarjeta(Restaurante r) {
        StackPane card = new StackPane();
        card.getStyleClass().add("restaurant-card");
        card.setPrefWidth(390);
        card.setPrefHeight(340);
        card.setMinHeight(340);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setCursor(Cursor.HAND);

        ImageView image = new ImageView(cargarImagen(r.getNombre()));
        image.setFitHeight(340);
        image.setFitWidth(390);
        image.setPreserveRatio(false);
        Rectangle clip = new Rectangle(390, 340);
        clip.setArcWidth(44); clip.setArcHeight(44);
        image.setClip(clip);

        BorderPane overlay = new BorderPane();
        overlay.getStyleClass().add("card-overlay");
        overlay.setPadding(new Insets(16, 17, 17, 17));

        Label state = new Label(estadoTexto(r));
        state.getStyleClass().add(r.getEstadoActual() == EstadoRestaurante.ABIERTO ? "status-open" : "status-closed");

        boolean favorite = favoritos.contains(r.getNombre());
        Button fav = new Button(favorite ? "♥" : "♡");
        fav.setAccessibleText(favorite ? "Quitar de favoritos" : "Agregar a favoritos");
        fav.setTooltip(new Tooltip(favorite ? "Quitar de favoritos" : "Agregar a favoritos"));
        fav.getStyleClass().add("favorite-button");
        if (favorite) fav.getStyleClass().add("selected");
        fav.setOnAction(e -> { if (accionAlternarFavorito != null) accionAlternarFavorito.accept(r); });
        HBox top = new HBox(state, spacer(), fav);
        top.setAlignment(Pos.TOP_LEFT);
        overlay.setTop(top);

        Label name = new Label(r.getNombre());
        name.getStyleClass().add("card-title");
        Label location = new Label("⌖  " + r.getUbicacion());
        location.getStyleClass().add("card-muted");
        Integer avg = tiemposPromedio.get(r.getNombre());
        Label wait = new Label(avg == null ? "◷  Sin reportes recientes" : "◷  " + avg + " min de espera promedio");
        wait.getStyleClass().add("card-wait");

        Button report = new Button("Reportar fila");
        report.getStyleClass().add("card-action");
        report.setDisable(r.getEstadoActual() != EstadoRestaurante.ABIERTO);
        report.setOnAction(e -> { if (accionReportar != null) accionReportar.accept(r); });

        Button open = new Button("Está abierto");
        open.getStyleClass().add("card-action");
        Button closed = new Button("Reportar cierre");
        closed.getStyleClass().add("card-action-danger");
        open.setDisable(!r.estaEnHorario()); closed.setDisable(!r.estaEnHorario());
        open.setOnAction(e -> { if (accionVerificarCierre != null) accionVerificarCierre.accept(r, false); });
        closed.setOnAction(e -> { if (accionVerificarCierre != null) accionVerificarCierre.accept(r, true); });
        HBox actions = new HBox(8, report, open, closed);
        for (Button button : List.of(report, open, closed)) {
            HBox.setHgrow(button, Priority.ALWAYS);
            button.setMaxWidth(Double.MAX_VALUE);
        }
        VBox info = new VBox(5, name, location, wait, space(5), actions);
        overlay.setBottom(info);
        card.getChildren().addAll(image, overlay);
        card.setOnMouseClicked(e -> {
            Node target = (Node) e.getTarget();
            while (target != null) {
                if (target instanceof Button) return;
                target = target.getParent();
            }
            if (accionVerDetalle != null) accionVerDetalle.accept(r);
        });
        return card;
    }

    private void actualizarQuick() {
        quickContent.getChildren().clear();
        List<Restaurante> conTiempo = restaurantes.stream()
                .filter(r -> r.getEstadoActual() == EstadoRestaurante.ABIERTO && tiemposPromedio.containsKey(r.getNombre()))
                .sorted(Comparator.comparingInt(r -> tiemposPromedio.get(r.getNombre()))).toList();
        if (conTiempo.isEmpty()) {
            Label waiting = new Label("Los próximos reportes aparecerán aquí");
            waiting.getStyleClass().add("muted");
            quickContent.getChildren().add(waiting);
            return;
        }
        LinkedHashSet<Restaurante> picks = new LinkedHashSet<>();
        picks.add(conTiempo.get(0));
        conTiempo.stream().filter(r -> favoritos.contains(r.getNombre())).findFirst().ifPresent(picks::add);
        for (Restaurante r : conTiempo) { if (picks.size() >= 2) break; picks.add(r); }
        picks.forEach(r -> quickContent.getChildren().add(quickChip(r, r.equals(conTiempo.get(0)))));
    }

    private Pane quickChip(Restaurante r, boolean fastest) {
        Label icon = new Label(favoritos.contains(r.getNombre()) ? "♥" : "◷");
        icon.setStyle("-fx-font-size:20px;-fx-text-fill:#E85D3F;");
        Label name = new Label(r.getNombre() + " · " + tiemposPromedio.get(r.getNombre()) + " min");
        name.setStyle("-fx-font-weight:bold;-fx-text-fill:#3B2A20;");
        Label note = new Label(fastest ? "La fila más rápida" : "Uno de tus favoritos");
        note.getStyleClass().add("muted");
        HBox chip = new HBox(10, icon, new VBox(1, name, note));
        chip.getStyleClass().add("quick-chip");
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(10, 14, 10, 14));
        return chip;
    }

    private String estadoTexto(Restaurante r) {
        if (!r.estaEnHorario()) return "CERRADO POR HORARIO";
        return switch (r.getEstadoActual()) {
            case ABIERTO -> "● ABIERTO";
            case POSIBLEMENTE_CERRADO -> "● POSIBLEMENTE CERRADO";
            case CERRADO -> "● CERRADO";
        };
    }

    private Image cargarImagen(String nombre) {
        String file = switch (nombre.toLowerCase(Locale.ROOT)) {
            case "&cafe" -> "&cafe.jfif";
            case "barista" -> "cafe_barista.jpg";
            case "gitane" -> "cafe_gitane.jpg";
            case "gogreen" -> "gogreen.jfif";
            case "sarita" -> "sarita.jfif";
            default -> "cafe_barista.jpg";
        };
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + file)));
    }

    private void animarEntrada() {
        int i = 0;
        for (Node node : tarjetas.getChildren()) {
            node.setOpacity(0); node.setTranslateY(14);
            FadeTransition fade = new FadeTransition(Duration.millis(260), node);
            fade.setToValue(1); fade.setDelay(Duration.millis(i * 45L));
            TranslateTransition move = new TranslateTransition(Duration.millis(260), node);
            move.setToY(0); move.setDelay(Duration.millis(i * 45L)); move.setInterpolator(Interpolator.EASE_OUT);
            new ParallelTransition(fade, move).play(); i++;
        }
    }

    public void mostrarFavoritos(Set<String> favoritos) { this.favoritos = new HashSet<>(favoritos); aplicarFiltros(); }
    public void mostrarPromedios(Map<String, Integer> promedios) { tiemposPromedio = new HashMap<>(promedios); aplicarFiltros(); }
    public void mostrarSoloFavoritos(boolean value) {
        mostrandoReportes = false; soloFavoritos = value;
        titulo.setText(value ? "Tus restaurantes favoritos" : "¿Dónde quieres comer hoy?");
        campoBusqueda.setDisable(false); filtroEstado.setDisable(false);
        setNav(value ? botonFavoritos : botonInicio); aplicarFiltros();
    }

    public void mostrarMisReportes(List<Reporte> reportes) {
        mostrandoReportes = true; titulo.setText("Mis reportes");
        campoBusqueda.setDisable(true); filtroEstado.setDisable(true); setNav(botonReportes);
        tarjetas.getChildren().clear(); resumen.setText(reportes.size() == 1 ? "1 reporte" : reportes.size() + " reportes");
        if (reportes.isEmpty()) {
            Label empty = new Label("Todavía no has realizado reportes"); empty.getStyleClass().add("empty-state");
            tarjetas.getChildren().add(empty);
        } else reportes.forEach(r -> tarjetas.getChildren().add(reporteCard(r)));
        animarEntrada();
    }

    private Pane reporteCard(Reporte r) {
        VBox card = new VBox(9);
        card.getStyleClass().add("report-card"); card.setPadding(new Insets(20)); card.setPrefWidth(360);
        Label title = new Label(r.getNombreRestaurante());
        title.setStyle("-fx-font-family:Georgia;-fx-font-size:21px;-fx-font-weight:bold;-fx-text-fill:#3B2A20;");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm");
        card.getChildren().addAll(title, new Label("Personas en fila: " + r.getCantidadPersonas()),
                new Label("Tiempo de espera: " + r.getTiempoEspera() + " minutos"),
                new Label(r.getFechaHora().format(df)));
        return card;
    }

    private void setNav(Button active) {
        for (Button b : List.of(botonInicio, botonFavoritos, botonReportes)) b.getStyleClass().remove("active");
        active.getStyleClass().add("active");
    }

    public void mostrarMensaje(String title, String text) { alert(Alert.AlertType.INFORMATION, title, text); }
    public void mostrarError(String text) { alert(Alert.AlertType.ERROR, "Algo salió mal", text); }
    private void alert(Alert.AlertType type, String title, String text) {
        Alert a = new Alert(type); a.initOwner(stage); a.initModality(Modality.WINDOW_MODAL);
        a.setTitle("FastQueue"); a.setHeaderText(title); a.setContentText(text);
        a.getDialogPane().getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm()); a.showAndWait();
    }

    private Region spacer() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }
    private Region space(double h) { Region r = new Region(); r.setMinHeight(h); return r; }
    public void show() { stage.show(); }
    public void close() { stage.close(); }
    public Stage getStage() { return stage; }
    public Button getBotonActualizar() { return botonActualizar; }
    public Button getBotonInicio() { return botonInicio; }
    public Button getBotonFavoritos() { return botonFavoritos; }
    public void setAccionReportar(Consumer<Restaurante> a) { accionReportar = a; }
    public void setAccionAlternarFavorito(Consumer<Restaurante> a) { accionAlternarFavorito = a; }
    public void setAccionVerificarCierre(BiConsumer<Restaurante, Boolean> a) { accionVerificarCierre = a; }
    public void setAccionVerReportes(Runnable a) { accionVerReportes = a; }
    public void setAccionVerDetalle(Consumer<Restaurante> a) { accionVerDetalle = a; }
}
