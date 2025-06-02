package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * PositionText is a StackPane that displays a single letter or number
 * centered within a fixed-size square. Used for row and column labels
 * on the game board.
 */
public class PositionText extends StackPane {

    private static final int SIZE = 100;

    /**
     * Constructs a PositionText with the given label string, formats it in
     * uppercase Verdana bold, and adds it to the StackPane. Also sets the
     * background to light gray with a black border.
     *
     * @param label the text to display (e.g., "A", "1"), converted to uppercase
     */
    public PositionText(String label) {
        Text text = new Text(label.toUpperCase());
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        text.setStyle("-fx-fill: black;");

        this.setMinSize(SIZE, SIZE);
        this.setMaxSize(SIZE, SIZE);
        this.setPrefSize(SIZE, SIZE);
        // add the Text node to the StackPane
        this.getChildren().add(text);
        this.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
    }
}
