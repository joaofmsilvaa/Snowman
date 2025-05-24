package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.MobileEntity;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

public class EntityButton extends Button {

    private static Image backgroundImage;

    private int size = 100;

    public EntityButton(MobileEntity content) {
        ImageView imageView = null;

        if (content == MobileEntity.MONSTER) {
            backgroundImage = new Image("/monster1.png");
            imageView = new ImageView(backgroundImage);
        } else if (content == MobileEntity.SNOWBALL) {
            backgroundImage = new Image("/ballsmall.png");
            imageView = new ImageView(backgroundImage);
        }

        if (imageView != null) {
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);

            this.setGraphic(imageView);
        } else {
            this.setGraphic(null); // Remove qualquer gráfico existente
        }

        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setPrefSize(size, size);
        this.setPadding(javafx.geometry.Insets.EMPTY);
        this.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");
        this.setFocusTraversable(false);
    }

    public void setEntity(MobileEntity entity) {
        switch (entity){
            case SNOWBALL:
                backgroundImage = new Image("/snowball.png");
                break;
            case MONSTER:
                backgroundImage = new Image("/monster1.png");
                break;
        }
    }

}
