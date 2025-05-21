package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;

public class BoardButton extends Button {

    private static final Image GRASS = new Image("/grass.png");

    private ImageView imageView;

    public BoardButton() {
        this.imageView = new ImageView(GRASS);
        this.setGraphic(imageView);

    }

}
