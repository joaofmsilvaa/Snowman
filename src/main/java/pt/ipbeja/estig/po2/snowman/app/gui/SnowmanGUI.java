package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;

public class SnowmanGUI extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        SnowmanBoard board = new SnowmanBoard();
        MobileBoard mobileBoard = new MobileBoard();

        StackPane root = new StackPane();
        root.getChildren().addAll(board, mobileBoard);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}