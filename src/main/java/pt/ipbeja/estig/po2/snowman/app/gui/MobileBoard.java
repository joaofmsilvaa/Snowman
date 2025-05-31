package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.estig.po2.snowman.app.model.*;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

public class MobileBoard extends GridPane implements View {

    private final BoardModel board;
    private final EntityButton[][] buttons;
    private String playerName;
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
        });
    }


    public void drawBoard() {
        this.getChildren().clear();
        int rows = board.getRowCount();
        int cols = board.getColCount();

        for (int col = 0; col < cols; col++) {
            PositionText letter = new PositionText(Character.toString((char) ('A' + col)));
            this.add(letter, col + 1, 0);
        }

        for (int row = 0; row < rows; row++) {
            PositionText number = new PositionText(String.valueOf(row + 1));
            this.add(number, 0, row + 1);

            for (int col = 0; col < cols; col++) {
                Monster monster = board.getMonster();
                MobileEntity entity = MobileEntity.EMPTY;
                Snowball snowball = board.getSnowballInPosition(row, col);
                boolean hasMonster = monster.getRow() == row && monster.getCol() == col;

                if (hasMonster) {
                    entity = MobileEntity.MONSTER;
                } else if (snowball != null) {
                    entity = MobileEntity.SNOWBALL;
                }

                EntityButton button = new EntityButton(entity);
                this.add(button, col + 1, row + 1);
                buttons[row][col] = button;
            }
        }
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getMoveCount() {
        return moveCount;
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
        buttons[oldPosition.getRow()][oldPosition.getCol()].clearEntity();

        if (board.getMonster().getRow() == oldPosition.getRow() && board.getMonster().getCol() == oldPosition.getCol()) {
            buttons[oldPosition.getRow()][oldPosition.getCol()].setMonsterVisible(true);
        }

        int newRow = snowball.getRow();
        int newCol = snowball.getCol();
        buttons[newRow][newCol].setSnowballType(snowball.getType());

        if (board.getMonster().getRow() == newRow && board.getMonster().getCol() == newCol) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onSnowmanCreated(Position snowmanPos, SnowballType newType) {
        String name = board.getPlayerName();
        int moves = board.getMoveCount();
        buttons[snowmanPos.getRow()][snowmanPos.getCol()]
                .setSnowballType(SnowballType.COMPLETE);


        // Mostrar alerta Game Over
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("Parabéns! O boneco de neve foi criado. " +
                    "\nJogador: " + name + "\nMovimentos: " + moves);
            alert.showAndWait();

            // Voltar ao menu principal
            Stage primaryStage = (Stage) this.getScene().getWindow();
            primaryStage.close();

        });
    }

    @Override
    public void onSnowballStacked(Position snowballPos, SnowballType newType) {
        buttons[snowballPos.getRow()][snowballPos.getCol()].setSnowballType(newType);
        Monster monster = board.getMonster();
        if (monster.getRow() == snowballPos.getRow() && monster.getCol() == snowballPos.getCol()) {
            buttons[snowballPos.getRow()][snowballPos.getCol()].setMonsterVisible(true);
        }
    }

    @Override
    public void onSnowballUnstacked(Snowball top, Snowball bottom) {
        int topRow = top.getRow();
        int topCol = top.getCol();
        int bottomRow = bottom.getRow();
        int bottomCol = bottom.getCol();

        buttons[topRow][topCol].clearEntity();
        buttons[bottomRow][bottomCol].clearEntity();

        buttons[topRow][topCol].setSnowballType(top.getType());
        buttons[bottomRow][bottomCol].setSnowballType(bottom.getType());

        Monster monster = board.getMonster();
        if (monster.getRow() == topRow && monster.getCol() == topCol) {
            buttons[topRow][topCol].setMonsterVisible(true);
        }
        if (monster.getRow() == bottomRow && monster.getCol() == bottomCol) {
            buttons[bottomRow][bottomCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onMonsterCleared(Position monsterPosition) {

        buttons[monsterPosition.getRow()][monsterPosition.getCol()].setMonsterVisible(false);
    }

}
