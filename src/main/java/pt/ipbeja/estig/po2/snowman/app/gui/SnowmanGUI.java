package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;


public class SnowmanGUI extends Application {
    private final String mapFileName;
    private final String playerName;

    SnowmanGUI(String mapFileName, String playerName) {
        this.mapFileName = mapFileName;
        this.playerName = playerName;
    }

    @Override
    public void start(Stage stage) {
        MapReader reader = new MapReader();
        BoardModel boardModel = reader.loadMapFromFile("/" + this.mapFileName);
        Game game = new Game(this.playerName);
        boardModel.setGame(game);

        MoveHistoryPane moveHistoryPane = new MoveHistoryPane();
        boardModel.setMoveListener(moveHistoryPane);

        SnowmanBoard board = new SnowmanBoard(boardModel);
        MobileBoard mobileBoard = new MobileBoard(boardModel);

        StackPane root = new StackPane();

        root.getChildren().addAll(board, mobileBoard);
        VBox vBox = new VBox(root,moveHistoryPane);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Snowman Game");
        stage.show();
    }

}