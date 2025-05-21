package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;

public class BoardButton extends Button {

    private static final Image EMPTY = new Image("/images/empty.png");

    private ImageView imageView;

    public BoardButton(BoardModel board, int col, int row) {
        this.imageView = new ImageView(EMPTY);
        this.setGraphic(imageView);
        this.setPrefSize(100,100);

    }

}
