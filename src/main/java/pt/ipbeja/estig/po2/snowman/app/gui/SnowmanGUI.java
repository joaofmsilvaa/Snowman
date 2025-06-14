package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
 * @author João Silva
 * @author Paulo Neves
 */
public class SnowmanGUI extends Application {
    private final String mapFileName;
    private final String playerName;
    private BoardModel boardModel;
    private MapReader reader;

    /// Constructs the GUI launcher with the specified map file name and player name.
    public SnowmanGUI(String mapFileName, String playerName) {
        this.mapFileName = mapFileName;
        this.playerName = playerName;

        //Load the map from resource file
        reader = new MapReader();
        boardModel = reader.loadMapFromFile("/" + this.mapFileName);
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

        //Create a Game instance for tracking moves and player info
        Game game = new Game(this.playerName, reader.getMapName());
        boardModel.setGame(game);
        boardModel.setMapFileName(this.mapFileName);
        // ScoreBoard
        ScoreBoard scoreBoard = new ScoreBoard(game.getMapName());
        boardModel.setScoreListener(scoreBoard); // permite ao modelo atualizar o painel

        // Set up the ScoreBoard and register it as a listener for scores
        MoveHistoryPane moveHistoryPane = new MoveHistoryPane();
        boardModel.setMoveListener(moveHistoryPane);

        // Create a static board terrain and then the mobile overlay
        SnowmanBoard board = new SnowmanBoard(boardModel);
        MobileBoard mobileBoard = new MobileBoard(boardModel);

        //show the scene
        Scene scene = new Scene(getRoot(board, mobileBoard, moveHistoryPane, scoreBoard));
        stage.setScene(scene);
        stage.setTitle("Snowman Game");
        stage.show();
    }

    public BorderPane getRoot(SnowmanBoard board, MobileBoard mobileBoard, MoveHistoryPane moveHistoryPane, ScoreBoard scoreBoard) {

        //Stack the static terrain and the mobile overlay on top of each other
        StackPane boardPane = new StackPane(board, mobileBoard);

        VBox stateMenuBar = stateMenuBar();

        //Create a VBox on the left: board + move history
        VBox leftPane = new VBox(stateMenuBar, boardPane, moveHistoryPane);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));

        //Arrange the main layout in a BorderPane:
        //  - Left: board and move history
        //  - Right: scoreboard
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setRight(scoreBoard);

        return root;
    }

    private VBox stateMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu editMenu = new Menu("Edit");

        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e -> boardModel.undo());

        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e -> boardModel.redo());

        editMenu.getItems().addAll(undoItem, redoItem);

        menuBar.getMenus().addAll(editMenu);

        return new VBox(menuBar);
    }
}
