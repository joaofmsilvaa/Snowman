package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.BoardListener;

/**
 * SnowmanBoard extends GridPane to render a static view of the board,
 * showing terrain (grass, snow, block, snowman) but not the moving entities.
 */
public class SnowmanBoard extends GridPane implements BoardListener {
    private final BoardModel board;
    private final BoardButton[][] buttons;
    private int rows;
    private int cols;

    /**
     * Constructs a SnowmanBoard tied to the given BoardModel.
     * Initializes row/column counts, creates a 2D array for buttons,
     * and draws the initial grid.
     *
     * @param boardModel the BoardModel containing the static terrain layout
     */
    public SnowmanBoard(BoardModel boardModel) {
        this.board = boardModel;
        rows = board.getRowCount();
        cols = board.getColCount();
        this.buttons = new BoardButton[rows][cols];
        board.setBoardListener(this);
        drawBoard();
    }

    /**
     * Draws (or redraws) the entire board:
     * - Clears any existing children from the GridPane.
     * - Adds column headers (A, B, C) in the top row.
     * - Adds row headers (1, 2, 3) in the first column.
     * - For each cell, gets the terrain type from the model and
     * creates a BoardButton to display the appropriate image.
     */
    public void drawBoard() {
        this.getChildren().clear();

        // Add column labels at row 0 with an offset of +1
        for (int col = 0; col < cols; col++) {
            PositionText letter = new PositionText(String.valueOf((char) ('A' + col)));
            this.add(letter, col + 1, 0); // offset +1
        }

        // For each row, add a row label and buttons for each terrain cell
        for (int row = 0; row < rows; row++) {
            PositionText number = new PositionText(String.valueOf(row + 1));
            this.add(number, 0, row + 1);

            for (int col = 0; col < cols; col++) {
                PositionContent terrain = board.getPositionContent(row, col);
                // Create a button that shows an image based on terrain type
                BoardButton button = new BoardButton(terrain);
                this.add(button, col + 1, row + 1);
                buttons[row][col] = button;
            }
        }
    }

    @Override
    public void onTerrainChanged(int row, int col, PositionContent newContent) {
        BoardButton button = buttons[row][col];
       
        if(button != null ){
            button.setContent(newContent);
        }


    }
}
