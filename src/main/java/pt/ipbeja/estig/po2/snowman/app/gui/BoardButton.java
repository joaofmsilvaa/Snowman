package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

public class BoardButton extends Button {

    private static final int SIZE = 100;

    public BoardButton(PositionContent content) {
        Image image = loadImageForContent(content);

        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(SIZE);
            imageView.setFitHeight(SIZE);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else {
            this.setGraphic(null);
        }

        this.setMinSize(SIZE, SIZE);
        this.setMaxSize(SIZE, SIZE);
        this.setPrefSize(SIZE, SIZE);
        this.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");
        this.setFocusTraversable(false);
    }

    private Image loadImageForContent(PositionContent content) {
        String imagePath = switch (content) {
            case NO_SNOW -> "/grass.png";
            case SNOW -> "/snow.png";
            case BLOCK -> "/block.png";
            case SNOWMAN -> "/snowman.png";
        };

        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + imagePath);
            return null;
        }
    }
}
