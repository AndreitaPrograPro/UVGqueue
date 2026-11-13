package view;

import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.EstadoRestaurante;
import model.Restaurante;

public class RestauranteDetalleFrame {
    private final Stage stage = new Stage();
    public RestauranteDetalleFrame(
                Restaurante restaurante,
                Integer promedio,
                Map<Integer, Double> promediosPorHora
        ) {
            stage.setTitle("FastQueue · " + restaurante.getNombre());

            Label name = new Label(restaurante.getNombre());
            name.getStyleClass().addAll("display", "h1");

            Label location = new Label("⌖  " + restaurante.getUbicacion());
            location.getStyleClass().add("muted");

            Label avg = new Label(
                    promedio == null
                            ? "Promedio reciente aún no disponible"
                            : promedio + " min de espera promedio reciente"
            );
            avg.setStyle(
                    "-fx-font-size:19px;-fx-font-weight:bold;"
                    + "-fx-text-fill:#4F7A52;"
            );

            boolean abierto =
                    restaurante.getEstadoActual() == EstadoRestaurante.ABIERTO;
            avg.setVisible(abierto);
            avg.setManaged(abierto);

            CategoryAxis x = new CategoryAxis();
    x.setLabel("Horario de registro");
    x.setAutoRanging(false);
    x.setTickLabelRotation(-45);
    x.setTickLabelFont(javafx.scene.text.Font.font(11));
    x.setStartMargin(25);
    x.setEndMargin(25);
    javafx.collections.ObservableList<String> intervalos =
            javafx.collections.FXCollections.observableArrayList();

    for (int hora = 7; hora < 20; hora++) {
        intervalos.add(String.format(
                "%02d–%02d", hora, hora + 1
        ));
    }

    x.setCategories(intervalos);

    NumberAxis y = new NumberAxis();
    y.setLabel("Promedio de espera (minutos)");
    y.setForceZeroInRange(true);
    y.setMinorTickVisible(false);

    BarChart<String, Number> chart = new BarChart<>(x, y);
    chart.setTitle("Promedio histórico por hora · Todos los días");
    chart.setLegendVisible(false);
    chart.setAnimated(false);
    chart.setMinHeight(380);
    chart.setCategoryGap(15);
    chart.setBarGap(0);

    XYChart.Series<String, Number> serie = new XYChart.Series<>();

    for (int hora = 7; hora < 20; hora++) {
        Double valor = promediosPorHora.get(hora);

        if (valor == null) {
            continue;
        }

        String intervalo = intervalos.get(hora - 7);
        XYChart.Data<String, Number> dato =
                new XYChart.Data<>(intervalo, valor);

        dato.nodeProperty().addListener((o, anterior, nodo) -> {
            if (nodo != null) {
                nodo.setStyle("-fx-bar-fill:#4F7A52;");
            }
        });

        serie.getData().add(dato);
    }

    chart.getData().add(serie);

        Label nota = new Label(
                promediosPorHora.isEmpty()
                        ? "Todavía no hay reportes para estos horarios."
                        : "Los intervalos sin barra no tienen reportes; "
                          + "no representan una espera de cero minutos."
        );
        nota.getStyleClass().add("muted");
        nota.setWrapText(true);

        HBox stats = new HBox(
                14,
                stat("Estado", restaurante.getEstadoActual()
                        .toString().replace('_', ' ')),
                stat("Horario", restaurante.getHoraApertura()
                        + " – " + restaurante.getHoraCierre())
        );

        VBox content = new VBox(
                14, name, location, avg, stats, chart, nota
        );
        content.setPadding(new Insets(30));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 900, 700);
        scene.getStylesheets().add(
                getClass().getResource("/styles/fastqueue.css")
                        .toExternalForm()
        );
        stage.setScene(scene);
    }

    private Pane stat(String title, String value) {
        Label t = new Label(title);
        t.getStyleClass().add("muted");

        Label v = new Label(value);
        v.setStyle("-fx-font-weight:bold;-fx-text-fill:#3B2A20;");

        VBox b = new VBox(4, t, v);
        b.getStyleClass().add("quick-chip");
        b.setPadding(new Insets(12));
        b.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(b, Priority.ALWAYS);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public void show() {
        stage.show();
    }
}