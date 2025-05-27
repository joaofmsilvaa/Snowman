package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartMenu {

    public static void show(Stage stage) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");

        startButton.setOnAction(e -> {
            SnowmanGUI game = new SnowmanGUI();
            game.start(stage);  // Inicia o jogo atual

        });

        layout.getChildren().add(startButton);

        Scene scene = new Scene(new StackPane(layout), 600, 600);
        stage.setScene(scene);
        stage.setTitle("Snowman - Menu");
        stage.show();
    }
}