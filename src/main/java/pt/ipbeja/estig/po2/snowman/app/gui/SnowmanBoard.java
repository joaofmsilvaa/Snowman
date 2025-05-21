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
        for (int row = 0; row < board.SIZE; row++) {
            for (int col = 0; col < board.SIZE; col++) {
                BoardButton button = new BoardButton();
                this.add(button, row, col);
                buttons[row][col] = button;
            }
        }
    }

}
