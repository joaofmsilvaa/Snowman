package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

public class BoardButton extends Button {

    private static Image backgroundImage;

    private ImageView imageView;
    private int size = 100;

    public BoardButton(PositionContent content) {
        if(content == PositionContent.SNOW){
            backgroundImage = new Image("/snow.png");
        }
        else{
            backgroundImage = new Image("/grass.png");
        }

        imageView = new ImageView(backgroundImage);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);

        this.setGraphic(imageView);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setPrefSize(size, size);

        this.setPadding(javafx.geometry.Insets.EMPTY);
        this.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");

        // Remove qualquer foco visual que apareça ao clicar
        this.setFocusTraversable(false);
    }

}
