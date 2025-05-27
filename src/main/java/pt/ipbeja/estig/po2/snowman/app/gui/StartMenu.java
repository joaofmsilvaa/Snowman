package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartMenu {

    public static void show(Stage stage) {

        Label title = new Label("Snow is hard to build");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");

        startButton.setOnAction(e -> {
            SnowmanGUI game = new SnowmanGUI();
            game.start(stage);  // Inicia o jogo atual

        });
        ImageView monsterView = new ImageView(new Image("/monster1.png"));
        monsterView.setFitWidth(80);
        monsterView.setFitHeight(80);

        ImageView snowmanView = new ImageView(new Image("/snowman.png"));
        snowmanView.setFitWidth(80);
        snowmanView.setFitHeight(80);

        HBox Images = new HBox(40);
        Images.setAlignment(Pos.CENTER);
        Images.setStyle("-fx-background-color: white;");
        Images.getChildren().addAll(monsterView, snowmanView);

        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        layout.getChildren().addAll(title, startButton, Images);

        Scene scene = new Scene(new StackPane(layout), 600, 600);
        stage.setScene(scene);
        stage.setTitle("Snowman - Menu");
        stage.show();
    }
}