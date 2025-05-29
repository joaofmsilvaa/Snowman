package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PositionText extends StackPane {

    private static final int SIZE = 100;

    public PositionText(String label) {
        Text text = new Text(label.toUpperCase());
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        text.setStyle("-fx-fill: black;");

        this.setMinSize(SIZE, SIZE);
        this.setMaxSize(SIZE, SIZE);
        this.setPrefSize(SIZE, SIZE);
        this.getChildren().add(text);
        this.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    }
}
