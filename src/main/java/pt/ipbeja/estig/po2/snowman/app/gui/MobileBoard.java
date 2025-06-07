package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.Optional;

/**
 * MobileBoard extends GridPane and implements View to render and update
 * the game board dynamically. It listens for key presses (W/A/S/D) to move
 * the monster, and updates buttons when the model notifies events.
 * @author João Silva
 * @author Paulo Neves
 */
public class MobileBoard extends GridPane implements View {

    private final BoardModel board;
    private final EntityButton[][] buttons;

    /**
     * Constructs a MobileBoard tied to the given BoardModel.
     * Registers itself as the View, draws the initial grid of buttons,
     * and sets up a key listener for monster movement.
     *
     * @param boardModel the BoardModel containing game logic
     */
    public MobileBoard(BoardModel boardModel) {
        this.board = boardModel;

        int rows = board.getRowCount();
        int cols = board.getColCount();
        this.buttons = new EntityButton[rows][cols];

        this.board.setView(this);
        drawBoard();

        this.setFocusTraversable(true);
        this.requestFocus();

        this.setOnKeyPressed(event -> {
            Direction direction = switch (event.getCode()) {
                case W -> Direction.UP;
                case A -> Direction.LEFT;
                case S -> Direction.DOWN;
                case D -> Direction.RIGHT;
                default -> null;
            };

            if (direction != null) {
                board.moveMonster(direction);
            }
        });
    }

    ///Draws the entire board: adds column labels, row labels,
    /// and creates an EntityButton for each cell
    public void drawBoard() {
        this.getChildren().clear();
        int rows = board.getRowCount();
        int cols = board.getColCount();

        // add column labels
        for (int col = 0; col < cols; col++) {
            PositionText letter = new PositionText(Character.toString((char) ('A' + col)));
            this.add(letter, col + 1, 0);
        }

        // for each row, add a row label and buttons for the columns
        for (int row = 0; row < rows; row++) {
            PositionText number = new PositionText(String.valueOf(row + 1));
            this.add(number, 0, row + 1);

            for (int col = 0; col < cols; col++) {
                Monster monster = board.getMonster();
                MobileEntity entity = MobileEntity.EMPTY;
                Snowball snowball = board.getSnowballInPosition(row, col);

                if (monster.getRow() == row && monster.getCol() == col) {
                    entity = MobileEntity.MONSTER;
                } else if (snowball != null) {
                    entity = MobileEntity.SNOWBALL;
                }

                EntityButton button = new EntityButton(entity);
                if(snowball != null){
                    button.setSnowballType(snowball.getType());
                }
                this.add(button, col + 1, row + 1);
                buttons[row][col] = button;
            }
        }
    }


    /**
     * Called when the monster has moved from one position to another.
     * Clears the monster graphic at its previous position and shows it
     * at the new position. Also increments move count.
     *
     * @param monsterPosition new position of the monster
     */
    @Override
    public void onMonsterMoved(Position monsterPosition) {
        Monster monster = board.getMonster();
        // Clear monster from its previous button
        buttons[monster.getPrevRow()][monster.getPrevCol()].setMonsterVisible(false);
        // Show monster on its new button
        buttons[monsterPosition.getRow()][monsterPosition.getCol()].setMonsterVisible(true);
    }

    /**
     * Called when a snowball has moved from oldPosition to its updated
     * position. Clears the old cell, updates the new cell with the correct
     * snowball image, and if the monster is on either cell, makes it visible.
     *
     * @param snowball    the Snowball object that moved
     * @param oldPosition the previous Position of the snowball
     */
    @Override
    public void onSnowballMoved(Snowball snowball, Position oldPosition) {
        // Clear the graphic at old position
        buttons[oldPosition.getRow()][oldPosition.getCol()].clearEntity();

        // If the monster was in that old cell, re-show the monster
        if (board.getMonster().getRow() == oldPosition.getRow() && board.getMonster().getCol() == oldPosition.getCol()) {
            buttons[oldPosition.getRow()][oldPosition.getCol()].setMonsterVisible(true);
        }

        // Show the snowball at its new cell
        int newRow = snowball.getRow();
        int newCol = snowball.getCol();
        buttons[newRow][newCol].setSnowballType(snowball.getType());


        // If the monster is now on top of the moved snowball, show it
        if (board.getMonster().getRow() == newRow && board.getMonster().getCol() == newCol) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }
    }

    /**
     * Called when a complete snowman is formed at snowmanPos.
     * Updates that button to show the complete snowman graphic,
     * then displays a Game Over alert and closes the stage.
     *
     * @param snowmanPos the Position where the snowman was created
     * @param newType    the SnowballType.COMPLETE
     */
    @Override
    public void onSnowmanCreated(Position snowmanPos, SnowballType newType) {
        String name = board.getPlayerName();
        int moves = board.getMoveCount();

        // Update the button to show a complete snowman
        buttons[snowmanPos.getRow()][snowmanPos.getCol()]
                .setSnowballType(SnowballType.COMPLETE);


        // Show an information alert Game Over
        Platform.runLater(() -> {
            ButtonType restartLevel = new ButtonType("Recomeçar Nível", ButtonBar.ButtonData.LEFT);
            ButtonType chooseLevel  = new ButtonType("Escolher Outro Nível", ButtonBar.ButtonData.LEFT);
            ButtonType cancel       = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);



            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("Parabéns! O boneco de neve foi criado. " +
                    "\nJogador: " + name + "\nMovimentos: " + moves);
            alert.getButtonTypes().setAll(restartLevel, chooseLevel, cancel);
            Stage current = (Stage) this.getScene().getWindow();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == restartLevel) {
                    // Chamado para reiniciar o mesmo nível
                    restartLevel();
                } else if (result.get() == chooseLevel) {
                    // Fechar esta janela e abrir ecrã de seleção de níveis
                    current.close();
                    new SnowmanGUI("map2.txt", name).start(new Stage());
                } else {
                    // Se cancelar, só fecha o diálogo
                    current.close();
                }
            }
        });
    }

    /**
     * Called when two snowballs have been stacked at snowballPos.
     * Updates that button to show the new stacked snowball type.
     * If the monster is also on that cell, makes it visible.
     *
     * @param snowballPos the Position where stacking occurred
     * @param newType     the resulting SnowballType after stacking
     */
    @Override
    public void onSnowballStacked(Position snowballPos, SnowballType newType) {
        buttons[snowballPos.getRow()][snowballPos.getCol()].setSnowballType(newType);
        Monster monster = board.getMonster();
        if (monster.getRow() == snowballPos.getRow() && monster.getCol() == snowballPos.getCol()) {
            buttons[snowballPos.getRow()][snowballPos.getCol()].setMonsterVisible(true);
        }
    }

    /**
     * Called when a stacked snowball has been unstacked into two separate snowballs.
     * Clears both top and bottom cells, then sets each cell’s graphic based on the new types.
     * If the monster occupies either cell, makes it visible.
     *
     * @param top    the Snowball that moves to a new cell
     * @param bottom the Snowball that remains in the original cell
     */
    @Override
    public void onSnowballUnstacked(Snowball top, Snowball bottom) {
        int topRow = top.getRow();
        int topCol = top.getCol();
        int bottomRow = bottom.getRow();
        int bottomCol = bottom.getCol();

        // Clear any existing graphics at both positions
        buttons[topRow][topCol].clearEntity();
        buttons[bottomRow][bottomCol].clearEntity();

        // Set each cell’s new snowball image
        buttons[topRow][topCol].setSnowballType(top.getType());
        buttons[bottomRow][bottomCol].setSnowballType(bottom.getType());

        Monster monster = board.getMonster();

        // If the monster is in the top cell, show it
        if (monster.getRow() == topRow && monster.getCol() == topCol) {
            buttons[topRow][topCol].setMonsterVisible(true);
        }

        // If the monster is in the bottom cell, show it
        if (monster.getRow() == bottomRow && monster.getCol() == bottomCol) {
            buttons[bottomRow][bottomCol].setMonsterVisible(true);
        }
    }

    /**
     * Called before the monster is redrawn at a new position. Hides
     * the monster graphic at the specified position.
     *
     * @param monsterPosition the Position where the monster was previously
     */
    @Override
    public void onMonsterCleared(Position monsterPosition) {

        buttons[monsterPosition.getRow()][monsterPosition.getCol()].setMonsterVisible(false);
    }

    @Override
    public void updateBoard() {
        drawBoard();
    }

    /**
     * Fecha a janela atual e relança um novo SnowmanGUI
     * com o mesmo ficheiro de mapa e nome de jogador.
     */
    private void restartLevel() {
        Stage current = (Stage) this.getScene().getWindow();
        current.close();
        // Usa agora o mapFileName que guardámos no BoardModel
        String mapName = board.getMapFileName();
        String player  = board.getGame().getPlayerName();
        new SnowmanGUI(mapName, player).start(new Stage());
    }

}
