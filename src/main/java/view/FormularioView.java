package view;

import java.util.function.BiConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FormularioView {
    private final Stage stage = new Stage();
    private final Spinner<Integer> personas = new Spinner<>(0, 500, 5);
    private final Slider minutos = new Slider(0, 120, 10);
    private final Label valorMinutos = new Label("10 minutos");
    private BiConsumer<Integer, Double> accionEnviar;

    public FormularioView() {
        stage.setTitle("FastQueue · Reportar fila");
        stage.initModality(Modality.APPLICATION_MODAL);
        Label title = new Label("¿Cómo está la fila?");
        title.getStyleClass().addAll("display", "h2");
        Label copy = new Label("Tu reporte ayuda a que otras personas decidan mejor.");
        copy.getStyleClass().add("muted"); copy.setWrapText(true);
        personas.setEditable(true); personas.setMaxWidth(Double.MAX_VALUE);
        minutos.setShowTickMarks(true); minutos.setShowTickLabels(true); minutos.setMajorTickUnit(30); minutos.setBlockIncrement(5);
        minutos.valueProperty().addListener((o,a,b) -> valorMinutos.setText(Math.round(b.doubleValue()) + " minutos"));
        valorMinutos.setStyle("-fx-font-weight:bold;-fx-text-fill:#4F7A52;");
        Button send = new Button("Compartir reporte"); send.getStyleClass().add("primary-button"); send.setMaxWidth(Double.MAX_VALUE);
        send.setOnAction(e -> { if (accionEnviar != null) accionEnviar.accept(personas.getValue(), minutos.getValue()); });
        VBox card = new VBox(15, title, copy, label("Personas aproximadas"), personas,
                label("Tiempo estimado"), minutos, valorMinutos, send);
        card.getStyleClass().add("form-card"); card.setPadding(new Insets(30));
        StackPane root = new StackPane(card); root.setPadding(new Insets(25)); root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 480, 510);
        scene.getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm());
        stage.setScene(scene);
    }

    private Label label(String text) { Label l = new Label(text); l.setStyle("-fx-font-weight:bold;"); return l; }
    public void setAccionEnviar(BiConsumer<Integer, Double> accion) { accionEnviar = accion; }
    public void show() { stage.show(); }
    public void close() { stage.close(); }
}
