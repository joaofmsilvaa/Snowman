package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBoard extends GridPane implements View {
    private BoardModel board;
    private BoardButton[][] buttons = new BoardButton[BoardModel.SIZE][BoardModel.SIZE];

    public SnowmanBoard() {
        this.board = new BoardModel();
        this.board.setView(this);
        drawBoard();

        this.setFocusTraversable(true);
        this.requestFocus();

        this.setOnKeyPressed(event -> {
            Direction direction = null;

            switch (event.getCode()) {
                case W -> direction = Direction.UP;
                case A -> direction = Direction.LEFT;
                case S -> direction = Direction.DOWN;
                case D -> direction = Direction.RIGHT;
            }

            if (direction != null) {
                board.moveMonster(direction);
            }
        });
    }

    public void drawBoard() {
        this.getChildren().clear();
        for (int row = 0; row < BoardModel.SIZE; row++) {
            for (int col = 0; col < BoardModel.SIZE; col++) {
                PositionContent terrain = board.getPositionContent(row, col);
                BoardButton button = new BoardButton(terrain);
                this.add(button, col, row);
                buttons[row][col] = button;
            }
        }
    }

    // Implementações da interface View:
    @Override
    public void onMonsterMoved(int row, int col) {
        for (int i = 0; i < BoardModel.SIZE; i++) {
            for (int j = 0; j < BoardModel.SIZE; j++) {
                buttons[i][j].setMonsterVisible(false);
            }
        }

        buttons[row][col].setMonsterVisible(true);
    }

    @Override
    public void onSnowballMoved(int row, int col) {
    }

    @Override
    public void onSnowmanCreated(int row, int col) {
        System.out.println("Boneco de neve criado em: " + row + ", " + col);
    }

    @Override
    public void onSnowballStacked(int row, int col, SnowballType newType) {
        System.out.println("Empilhamento na posição: " + row + ", " + col + " -> " + newType);
    }

    @Override
    public void onMonsterCleared(int row, int col) {
        buttons[row][col].setMonsterVisible(false);
    }
}