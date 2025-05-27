package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

public class MobileBoard extends GridPane implements View {

    private final BoardModel board;
    private final EntityButton[][] buttons;

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

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Monster monster = board.getMonster();
                MobileEntity entity = MobileEntity.EMPTY;
                Snowball snowball = board.snowballInPosition(row, col);
                boolean hasMonster = monster.getRow() == row && monster.getCol() == col;

                if (hasMonster) {
                    entity = MobileEntity.MONSTER;
                } else if (snowball != null) {
                    entity = MobileEntity.SNOWBALL;
                }

                EntityButton button = new EntityButton(entity);
                this.add(button, col, row);
                buttons[row][col] = button;
            }
        }
    }

    @Override
    public void onMonsterMoved(int row, int col) {
        Monster monster = board.getMonster();
        buttons[monster.getPrevRow()][monster.getPrevCol()].setMonsterVisible(false);
        buttons[row][col].setMonsterVisible(true);
    }

    @Override
    public void onSnowballMoved(Snowball snowball, int oldRow, int oldCol) {
        buttons[oldRow][oldCol].clearEntity();

        if (board.getMonster().getRow() == oldRow && board.getMonster().getCol() == oldCol) {
            buttons[oldRow][oldCol].setMonsterVisible(true);
        }

        int newRow = snowball.getRow();
        int newCol = snowball.getCol();
        buttons[newRow][newCol].setSnowballType(snowball.getType());

        if (board.getMonster().getRow() == newRow && board.getMonster().getCol() == newCol) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onSnowmanCreated(int row, int col, SnowballType newType) {
        buttons[row][col].setSnowballType(SnowballType.COMPLETE);
        System.out.println("Boneco de neve criado em: " + row + ", " + col);
    }

    @Override
    public void onSnowballStacked(int row, int col, SnowballType newType) {
        buttons[row][col].setSnowballType(newType);
        Monster monster = board.getMonster();
        if (monster.getRow() == row && monster.getCol() == col) {
            buttons[row][col].setMonsterVisible(true);
        }
        System.out.println("Empilhamento na posição: " + row + ", " + col + " -> " + newType);
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
    public void onMonsterCleared(int row, int col) {
        buttons[row][col].setMonsterVisible(false);
    }
}
