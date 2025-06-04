package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

public class MobileBoard extends GridPane implements View {

    private final BoardModel board;
    private final EntityButton[][] buttons;
    private int moveCount = 0;

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
            else if (event.getCode() == KeyCode.Z) {
                Monster monster = board.getMonster();
                Position prev = monster.undo();
                this.onMonsterMoved(prev);  // notify the view to update the monster's position
            } else if (event.getCode() == KeyCode.Y) {
                Monster monster = board.getMonster();
                Position next = monster.redo();
                this.onMonsterMoved(next);  // notify the view to update the monster's position
            }
        });
    }

    private boolean isMonsterAt(int row, int col) {
        Monster monster = board.getMonster();
        return monster != null && monster.getRow() == row && monster.getCol() == col;
    }

    public void drawBoard() {
        this.getChildren().clear();
        int rows = board.getRowCount();
        int cols = board.getColCount();

        for (int col = 0; col < cols; col++) {
            this.add(new PositionText(Character.toString((char) ('A' + col))), col + 1, 0);
        }

        for (int row = 0; row < rows; row++) {
            this.add(new PositionText(String.valueOf(row + 1)), 0, row + 1);

            for (int col = 0; col < cols; col++) {
                EntityButton button;
                Snowball snowball = board.getSnowballInPosition(row, col);

                if (isMonsterAt(row, col)) {
                    button = new EntityButton(MobileEntity.MONSTER);
                } else if (snowball != null) {
                    button = new EntityButton(MobileEntity.SNOWBALL);
                    button.setSnowballType(snowball.getType());
                } else {
                    button = new EntityButton(MobileEntity.EMPTY);
                }

                this.add(button, col + 1, row + 1);
                buttons[row][col] = button;
            }
        }
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    @Override
    public void onMonsterMoved(Position monsterPosition) {
        Monster monster = board.getMonster();
        buttons[monster.getPrevRow()][monster.getPrevCol()].setMonsterVisible(false);
        buttons[monsterPosition.getRow()][monsterPosition.getCol()].setMonsterVisible(true);
        incrementMoveCount();
    }

    @Override
    public void onSnowballMoved(Snowball snowball, Position oldPosition) {
        int oldRow = oldPosition.getRow(), oldCol = oldPosition.getCol();
        int newRow = snowball.getRow(), newCol = snowball.getCol();

        buttons[oldRow][oldCol].clearEntity();
        if (isMonsterAt(oldRow, oldCol)) {
            buttons[oldRow][oldCol].setMonsterVisible(true);
        }

        buttons[newRow][newCol].setSnowballType(snowball.getType());
        if (isMonsterAt(newRow, newCol)) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onSnowmanCreated(Position snowmanPos, SnowballType newType) {
        buttons[snowmanPos.getRow()][snowmanPos.getCol()].setSnowballType(newType);

        String name = board.getPlayerName();
        int moves = board.getMoveCount();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("Parabéns! O boneco de neve foi criado.\nJogador: " + name + "\nMovimentos: " + moves);
            alert.showAndWait();

            Stage primaryStage = (Stage) this.getScene().getWindow();
            primaryStage.close();
        });
    }

    @Override
    public void onSnowballStacked(Position snowballPos, SnowballType newType) {
        int row = snowballPos.getRow(), col = snowballPos.getCol();
        buttons[row][col].setSnowballType(newType);
        if (isMonsterAt(row, col)) {
            buttons[row][col].setMonsterVisible(true);
        }
    }

    @Override
    public void onSnowballUnstacked(Snowball top, Snowball bottom) {
        int topRow = top.getRow(), topCol = top.getCol();
        int bottomRow = bottom.getRow(), bottomCol = bottom.getCol();

        buttons[topRow][topCol].clearEntity();
        buttons[bottomRow][bottomCol].clearEntity();

        buttons[topRow][topCol].setSnowballType(top.getType());
        buttons[bottomRow][bottomCol].setSnowballType(bottom.getType());

        if (isMonsterAt(topRow, topCol)) {
            buttons[topRow][topCol].setMonsterVisible(true);
        }

        if (isMonsterAt(bottomRow, bottomCol)) {
            buttons[bottomRow][bottomCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onMonsterCleared(Position monsterPosition) {
        buttons[monsterPosition.getRow()][monsterPosition.getCol()].setMonsterVisible(false);
    }
}
