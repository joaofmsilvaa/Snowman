package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBoard extends GridPane implements View {

    private BoardModel board;
    private BoardButton[][] buttons = new BoardButton[board.SIZE][board.SIZE];

    // Construtor que inicializa o SnowmanBoard com o modelo do tabuleiro
    public SnowmanBoard() {
        this.board = new BoardModel();
       drawBoard();
    }

    public void drawBoard(){
        this.getChildren().clear();
        for (int row = 0; row < board.SIZE; row++) {
            for (int col = 0; col < board.SIZE; col++) {
                PositionContent terrain = board.getPositionContent(row, col);
                Snowball snowball = board.snowballInPosition(row, col);
                boolean hasMonster = board.getMonster().getRow() == row && board.getMonster().getCol() == col;

                BoardButton button;

                button = new BoardButton(terrain);

                this.add(button, col, row);
                buttons[row][col] = button;
            }
        }
    }

}
