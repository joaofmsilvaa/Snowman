package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;

public class SnowmanGUI extends Application {
    private final String mapFileName;
    private final String playerName;

    public SnowmanGUI(String mapFileName, String playerName) {
        this.mapFileName = mapFileName;
        this.playerName = playerName;
    }

    @Override
    public void start(Stage stage) {
        MapReader reader = new MapReader();
        BoardModel boardModel = reader.loadMapFromFile("/" + this.mapFileName);
        boardModel.setPlayerName(this.playerName);

        // ScoreBoard
        ScoreBoard scoreBoard = new ScoreBoard();
        boardModel.setScoreListener(scoreBoard); // permite ao modelo atualizar o painel

        MoveHistoryPane moveHistoryPane = new MoveHistoryPane();
        boardModel.setMoveListener(moveHistoryPane);

        SnowmanBoard board = new SnowmanBoard(boardModel);
        MobileBoard mobileBoard = new MobileBoard(boardModel);

        StackPane boardPane = new StackPane(board, mobileBoard);

        VBox leftPane = new VBox(boardPane, moveHistoryPane);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setRight(scoreBoard); // adicionar scoreboard à direita

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snowman Game");
        stage.show();
    }
}
