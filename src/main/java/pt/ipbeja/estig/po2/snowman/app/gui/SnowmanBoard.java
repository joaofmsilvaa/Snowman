package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBoard extends GridPane {
    private final BoardModel board;
    private final BoardButton[][] buttons;

    public SnowmanBoard(BoardModel boardModel) {
        this.board = boardModel;
        this.buttons = new BoardButton[BoardModel.SIZE][BoardModel.SIZE];
        drawBoard();
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
}