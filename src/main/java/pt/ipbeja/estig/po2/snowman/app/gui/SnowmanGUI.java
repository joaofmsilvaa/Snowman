package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;

/**
 * SnowmanGUI It initializes the game
 * by loading the selected map, setting up the model (BoardModel and Game),
 * and creating UI components for the static board, mobile elements,
 * move history, and scoreboard.
 */
public class SnowmanGUI extends Application {
    private final String mapFileName;
    private final String playerName;

    /// Constructs the GUI launcher with the specified map file name and player name.
    public SnowmanGUI(String mapFileName, String playerName) {
        this.mapFileName = mapFileName;
        this.playerName = playerName;
    }

    /**
     * Called by the JavaFX framework when the application starts.
     * Sets up the entire scene: loads the map into a BoardModel, creates a Game
     * object, registers listeners, and composes all UI panes into a BorderPane.
     *
     * @param stage the primary Stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        //Load the map from resource file
        MapReader reader = new MapReader();
        BoardModel boardModel = reader.loadMapFromFile("/" + this.mapFileName);

        //Create a Game instance for tracking moves and player info
        Game game = new Game(this.playerName, reader.getMapName());
        boardModel.setGame(game);

        // ScoreBoard
        ScoreBoard scoreBoard = new ScoreBoard(game.getMapName());
        boardModel.setScoreListener(scoreBoard); // permite ao modelo atualizar o painel

        // Set up the ScoreBoard and register it as a listener for scores
        MoveHistoryPane moveHistoryPane = new MoveHistoryPane();
        boardModel.setMoveListener(moveHistoryPane);

        // Create a static board terrain and then the mobile overlay
        SnowmanBoard board = new SnowmanBoard(boardModel);
        MobileBoard mobileBoard = new MobileBoard(boardModel);

        //Stack the static terrain and the mobile overlay on top of each other
        StackPane boardPane = new StackPane(board, mobileBoard);

        //Create a VBox on the left: board + move history
        VBox leftPane = new VBox(boardPane, moveHistoryPane);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));

        //Arrange the main layout in a BorderPane:
        //  - Left: board and move history
        //  - Right: scoreboard
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setRight(scoreBoard); // adicionar scoreboard à direita

        //show the scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snowman Game");
        stage.show();
    }
}
