package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

public class SnowmanBoard extends GridPane {
    private final BoardModel board;
    private final BoardButton[][] buttons;
    private int rows;
    private int cols;

    public SnowmanBoard(BoardModel boardModel) {
        this.board = boardModel;
        rows = board.getRowCount();
        cols = board.getColCount();
        this.buttons = new BoardButton[rows][cols];
        drawBoard();
    }

    public void drawBoard() {
        this.getChildren().clear();

        for (int col = 0; col < cols; col++) {
            PositionText letter = new PositionText(String.valueOf((char) ('A' + col)));
            this.add(letter, col + 1, 0); // offset +1 por causa da coluna de números
        }

        for (int row = 0; row < rows; row++) {
            PositionText number = new PositionText(String.valueOf(row + 1));
            this.add(number, 0, row + 1);

            for (int col = 0; col < cols; col++) {
                PositionContent terrain = board.getPositionContent(row, col);
                BoardButton button = new BoardButton(terrain);
                this.add(button, col + 1, row + 1);
                buttons[row][col] = button;
            }
        }
    }
}
