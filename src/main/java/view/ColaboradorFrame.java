package view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import model.Reporte;
import model.Restaurante;

public class ColaboradorFrame {

    private final Stage stage = new Stage();
    private final ComboBox<Restaurante> selectorRestaurante = new ComboBox<>();
    private final TableView<Reporte> tablaReportes = new TableView<>();

    private final Label totalReportes = new Label("Reportes: 0");
    private final Label totalEstudiantes = new Label("Estudiantes distintos: 0");

    private final Button botonActualizar = new Button("Actualizar");
    private final Button botonDescartar = new Button("Descartar reporte");
    private final Button botonExportar = new Button("Exportar CSV");

    private Consumer<Restaurante> accionSeleccionarRestaurante;
    private Consumer<Reporte> accionDescartar;
    private Runnable accionActualizar;
    private Runnable accionExportar;

    public ColaboradorFrame(String nombreUsuario) {
        construir(nombreUsuario);
        registrarEventos();
    }

    private void construir(String nombreUsuario) {
        Label marca = new Label("FastQueue");
        marca.getStyleClass().add("sidebar-brand");

        Label descripcion = new Label("Panel de colaborador");
        descripcion.getStyleClass().add("sidebar-subtitle");

        VBox lateral = new VBox(12, marca, descripcion);
        lateral.getStyleClass().add("sidebar");
        lateral.setPadding(new Insets(28, 20, 28, 20));
        lateral.setPrefWidth(230);

        Label saludo = new Label("Qué gusto verte, " + nombreUsuario);
        saludo.getStyleClass().add("muted");

        Label titulo = new Label("Administrar reportes");
        titulo.getStyleClass().addAll("display", "h1");

        selectorRestaurante.setPromptText("Selecciona un restaurante");
        selectorRestaurante.setPrefWidth(270);
        configurarSelector();

        botonActualizar.getStyleClass().add("primary-button");
        botonExportar.getStyleClass().add("primary-button");

        HBox herramientas = new HBox(
                12, selectorRestaurante, botonActualizar, botonExportar
        );
        herramientas.setAlignment(Pos.CENTER_LEFT);

        HBox resumen = new HBox(30, totalReportes, totalEstudiantes);

        configurarTabla();

        Label indicacion = new Label(
                "Selecciona un reporte para solicitar su descarte."
        );
        indicacion.getStyleClass().add("muted");
        indicacion.setWrapText(true);

        HBox acciones = new HBox(12, botonDescartar, indicacion);
        acciones.setAlignment(Pos.CENTER_LEFT);

        VBox contenido = new VBox(
                18, saludo, titulo, herramientas, resumen,
                tablaReportes, acciones
        );
        contenido.getStyleClass().add("content");
        contenido.setPadding(new Insets(28, 30, 28, 30));
        VBox.setVgrow(tablaReportes, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(lateral);
        root.setCenter(contenido);

        Scene scene = new Scene(root, 1180, 760);
        var css = getClass().getResource("/styles/fastqueue.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("FastQueue · Colaborador");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(650);
    }

    private void configurarSelector() {
        selectorRestaurante.setCellFactory(lista -> crearCeldaRestaurante());
        selectorRestaurante.setButtonCell(crearCeldaRestaurante());
    }

    private ListCell<Restaurante> crearCeldaRestaurante() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Restaurante restaurante, boolean vacio) {
                super.updateItem(restaurante, vacio);
                setText(vacio || restaurante == null
                        ? null : restaurante.getNombre());
            }
        };
    }

    private void configurarTabla() {
        TableColumn<Reporte, String> estudiante =
                new TableColumn<>("ID estudiante");

        estudiante.setCellValueFactory(dato -> {
            var usuario = dato.getValue().getUsuario();
            return new ReadOnlyStringWrapper(
                    usuario == null ? "Sin información"
                            : String.valueOf(usuario.getID())
            );
        });

        TableColumn<Reporte, String> personas =
                new TableColumn<>("Personas en fila");

        personas.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        String.valueOf(dato.getValue().getCantidadPersonas())
                )
        );

        TableColumn<Reporte, String> espera =
                new TableColumn<>("Espera (min)");

        espera.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        String.valueOf(dato.getValue().getTiempoEspera())
                )
        );

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        TableColumn<Reporte, String> fecha =
                new TableColumn<>("Fecha y hora");

        fecha.setCellValueFactory(dato -> {
            var fechaHora = dato.getValue().getFechaHora();
            return new ReadOnlyStringWrapper(
                    fechaHora == null ? "" : fechaHora.format(formato)
            );
        });

        tablaReportes.getColumns().add(estudiante);
        tablaReportes.getColumns().add(personas);
        tablaReportes.getColumns().add(espera);
        tablaReportes.getColumns().add(fecha);

        tablaReportes.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        tablaReportes.setPlaceholder(
                new Label("No hay reportes para mostrar.")
        );
    }

    private void registrarEventos() {
        selectorRestaurante.valueProperty().addListener(
                (observable, anterior, actual) -> {
                    mostrarReportes(List.of());

                    if (actual != null
                            && accionSeleccionarRestaurante != null) {
                        accionSeleccionarRestaurante.accept(actual);
                    }
                }
        );

        botonDescartar.disableProperty().bind(
                tablaReportes.getSelectionModel()
                        .selectedItemProperty().isNull()
        );

        botonExportar.disableProperty().bind(
                javafx.beans.binding.Bindings.isEmpty(
                        tablaReportes.getItems()
                )
        );

        botonActualizar.setOnAction(evento -> {
            if (accionActualizar != null) {
                accionActualizar.run();
            } else {
                mostrarError("El controlador todavía no está conectado.");
            }
        });

        botonDescartar.setOnAction(evento -> {
            Reporte seleccionado =
                    tablaReportes.getSelectionModel().getSelectedItem();

            if (seleccionado != null && accionDescartar != null) {
                accionDescartar.accept(seleccionado);
            } else {
                mostrarError("La acción de descarte no está conectada.");
            }
        });

        botonExportar.setOnAction(evento -> {
            if (accionExportar != null) {
                accionExportar.run();
            } else {
                mostrarError("La exportación todavía no está conectada.");
            }
        });
    }

    public void mostrarRestaurantes(List<Restaurante> asignados) {
        selectorRestaurante.getItems().setAll(asignados);
        selectorRestaurante.getSelectionModel().clearSelection();
        mostrarReportes(List.of());

        if (!asignados.isEmpty()) {
            selectorRestaurante.getSelectionModel().selectFirst();
        }
    }

    public void mostrarReportes(List<Reporte> reportes) {
        tablaReportes.getItems().setAll(reportes);
        totalReportes.setText("Reportes: " + reportes.size());

        Set<Integer> estudiantes = new HashSet<>();

        for (Reporte reporte : reportes) {
            if (reporte.getUsuario() != null
                    && reporte.getUsuario().getID() > 0) {
                estudiantes.add(reporte.getUsuario().getID());
            }
        }

        totalEstudiantes.setText(
                "Estudiantes distintos: " + estudiantes.size()
        );
    }

    public Restaurante getRestauranteSeleccionado() {
        return selectorRestaurante.getValue();
    }

    public void setAccionSeleccionarRestaurante(
            Consumer<Restaurante> accion) {
        accionSeleccionarRestaurante = accion;
    }

    public void setAccionDescartar(Consumer<Reporte> accion) {
        accionDescartar = accion;
    }

    public void setAccionActualizar(Runnable accion) {
        accionActualizar = accion;
    }

    public void setAccionExportar(Runnable accion) {
        accionExportar = accion;
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(stage);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.initOwner(stage);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
    public List<Reporte> getReportesVisibles() {
        return List.copyOf(tablaReportes.getItems());
    }
}