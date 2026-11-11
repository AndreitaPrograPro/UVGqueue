package view;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Restaurante;

public class RestauranteDetalleFrame {
    private final Stage stage = new Stage();

    public RestauranteDetalleFrame(Restaurante restaurante, Integer promedio, ArrayList<Integer> tiempos) {
        stage.setTitle("FastQueue · " + restaurante.getNombre());
        Label name = new Label(restaurante.getNombre()); name.getStyleClass().addAll("display", "h1");
        Label location = new Label("⌖  " + restaurante.getUbicacion()); location.getStyleClass().add("muted");
        Label avg = new Label(promedio == null ? "Sin reportes suficientes" : promedio + " min de espera promedio");
        avg.setStyle("-fx-font-size:19px;-fx-font-weight:bold;-fx-text-fill:#4F7A52;");

        NumberAxis x = new NumberAxis(); x.setLabel("Reporte"); x.setForceZeroInRange(false);
        NumberAxis y = new NumberAxis(); y.setLabel("Minutos");
        LineChart<Number, Number> chart = new LineChart<>(x, y); chart.setLegendVisible(false); chart.setAnimated(true);
        chart.setCreateSymbols(true); chart.setMinHeight(350);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < tiempos.size(); i++) series.getData().add(new XYChart.Data<>(i + 1, tiempos.get(i)));
        chart.getData().add(series);

        HBox stats = new HBox(14, stat("Estado", restaurante.getEstadoActual().toString().replace('_',' ')),
                stat("Horario", restaurante.getHoraApertura() + " – " + restaurante.getHoraCierre()),
                stat("Reportes", String.valueOf(tiempos.size())));
        VBox content = new VBox(14, name, location, avg, stats, chart); content.setPadding(new Insets(30));
        ScrollPane scroll = new ScrollPane(content); scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 760, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm());
        stage.setScene(scene);
    }

    private Pane stat(String title, String value) {
        Label t = new Label(title); t.getStyleClass().add("muted");
        Label v = new Label(value); v.setStyle("-fx-font-weight:bold;-fx-text-fill:#3B2A20;");
        VBox b = new VBox(4, t, v); b.getStyleClass().add("quick-chip"); b.setPadding(new Insets(12)); b.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(b, Priority.ALWAYS); b.setMaxWidth(Double.MAX_VALUE); return b;
    }

    public void show() { stage.show(); }
}
