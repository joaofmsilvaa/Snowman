package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.MapReader;


public class SnowmanGUI extends Application {

    @Override
    public void start(Stage stage) {
        String[][] mapa = {
                {"S", "S", "S", "S", "S"},
                {"X", "SB", "SB", "SB", "X"},
                {"M", "X", "X", "X", "X"},
                {"B", "S", "S", "S", "S"},
                {"X", "X", "X", "X", "X"}
        };

        MapReader reader = new MapReader();
        BoardModel boardModel = reader.parseMap(mapa);

        SnowmanBoard board = new SnowmanBoard(boardModel);
        MobileBoard mobileBoard = new MobileBoard(boardModel);

        StackPane root = new StackPane();
        root.getChildren().addAll(board, mobileBoard);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snowman Game");
        stage.show();
    }
}