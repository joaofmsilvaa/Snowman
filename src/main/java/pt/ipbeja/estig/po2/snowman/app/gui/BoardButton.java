package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

public class BoardButton extends Button {

    private static final Image snowImage = new Image("/snow.png");
    private static final Image grassImage = new Image("/grass.png");
    private static final Image monsterImage = new Image("/monster1.png"); // Certifica-te que tens esta imagem!

    private final ImageView backgroundImageView;
    private final ImageView monsterImageView;

    private final int size = 100;

    public BoardButton(PositionContent content) {
        Image background = (content == PositionContent.SNOW) ? snowImage : grassImage;

        this.backgroundImageView = new ImageView(background);
        this.backgroundImageView.setFitWidth(size);
        this.backgroundImageView.setFitHeight(size);
        this.backgroundImageView.setPreserveRatio(false);

        this.monsterImageView = new ImageView(monsterImage);
        this.monsterImageView.setFitWidth(size);
        this.monsterImageView.setFitHeight(size);
        this.monsterImageView.setPreserveRatio(true);
        this.monsterImageView.setVisible(false); // Começa invisível

        this.setGraphic(backgroundImageView);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setPrefSize(size, size);
        this.setPadding(javafx.geometry.Insets.EMPTY);
        this.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");
        this.setFocusTraversable(false);

        // Empilha o monstro sobre a imagem de fundo
        javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane(backgroundImageView, monsterImageView);
        this.setGraphic(stack);
    }

    public void setMonsterVisible(boolean visible) {
        this.monsterImageView.setVisible(visible);
    }
}
