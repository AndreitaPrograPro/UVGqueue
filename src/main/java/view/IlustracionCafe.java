package view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;

/** Ilustración vectorial de la tacita: escala sin pixelarse y no necesita PNG. */
public class IlustracionCafe extends Pane {
    public IlustracionCafe() {
        setPrefSize(320, 280);
        setMinSize(260, 230);

        Circle halo = new Circle(155, 142, 112, Color.web("#F4B400", .14));
        Circle accent = new Circle(116, 124, 67, Color.web("#4F7A52", .22));

        SVGPath cup = path(
                "M55 105 L67 190 Q70 215 96 215 L198 215 Q224 215 228 190 L240 105 Z",
                5.2);
        SVGPath handle = path("M239 125 C292 112 306 185 237 191", 5.2);
        SVGPath steam1 = path("M100 92 C75 62 126 45 103 15", 4.3);
        SVGPath steam2 = path("M151 92 C126 59 177 44 155 8", 4.3);
        SVGPath steam3 = path("M202 92 C178 65 224 50 205 23", 4.3);

        Line plate = new Line(35, 232, 267, 232);
        plate.setStroke(Color.web("#FFF7E6", .92));
        plate.setStrokeWidth(4.2);
        plate.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        getChildren().addAll(halo, accent, cup, handle, steam1, steam2, steam3, plate);
    }

    private SVGPath path(String content, double width) {
        SVGPath p = new SVGPath();
        p.setContent(content);
        p.setFill(Color.TRANSPARENT);
        p.setStroke(Color.web("#FFF7E6", .92));
        p.setStrokeWidth(width);
        p.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        p.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        return p;
    }
}
