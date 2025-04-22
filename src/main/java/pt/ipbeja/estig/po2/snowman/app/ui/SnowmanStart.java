package pt.ipbeja.estig.po2.snowman.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SnowmanStart extends Application {
    @Override
    public void start(Stage stage) {
        SnowmanBoard board = new SnowmanBoard();
        Scene scene = new Scene(board);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}