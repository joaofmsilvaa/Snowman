package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;

public class SnowmanGUI extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        SnowmanBoard board = new SnowmanBoard();
        Scene scene = new Scene(board);
        stage.setScene(scene);
        stage.show();

    }
}