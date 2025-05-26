package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.MobileEntity;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;
import pt.ipbeja.estig.po2.snowman.app.model.SnowballType;

import java.io.InputStream;

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
        Image image = null;

        switch (entity) {
            case SNOWBALL -> image = new Image("/snowball.png");
            case MONSTER -> image = new Image("/monster1.png");
            case EMPTY -> {
            }
        }

        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else {
            this.setGraphic(null);
        }
    }

    public void clearEntity() {

        this.setGraphic(null);
    }

    private boolean hasMonster = false;
    private boolean hasSnowball = false;

    public void setMonsterVisible(boolean visible) {
        this.hasMonster = visible;
        updateGraphic();
    }

    public void setSnowballVisible(boolean visible) {
        this.hasSnowball = visible;
        updateGraphic();
    }

    public void setSnowballType(SnowballType type) {
        String path = switch (type) {
            case SMALL -> "/ballsmall.png";
            case MID -> "/ballmedium.png";
            case BIG -> "/bigball.png";
            case MID_SMALL -> "/midsmall.png";
            case BIG_SMALL -> "/bigsmall.png";
            case BIG_MID -> "/bigmidium.png";
            case COMPLETE -> "/snowman.png";
        };

        Image image = new Image(path);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);
        this.setGraphic(imageView);
    }

    private void updateGraphic() {
        if (hasMonster) {
            ImageView imageView = new ImageView(new Image("/monster1.png"));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else if (hasSnowball) {
            ImageView imageView = new ImageView(new Image("/ballsmall.png"));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            this.setGraphic(imageView);
        } else {
            this.setGraphic(null);
        }
    }



}
