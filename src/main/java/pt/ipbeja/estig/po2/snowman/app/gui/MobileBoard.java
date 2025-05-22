package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class MobileBoard extends GridPane implements View {

    private BoardModel board;
    private EntityButton[][] buttons = new EntityButton[board.SIZE][board.SIZE];

    // Construtor que inicializa o SnowmanBoard com o modelo do tabuleiro
    public MobileBoard() {
        this.board = new BoardModel();
        drawBoard();
    }

    public void drawBoard(){
        this.getChildren().clear();
        for (int row = 0; row < board.SIZE; row++) {
            for (int col = 0; col < board.SIZE; col++) {
                Monster monster = board.getMonster();
                MobileEntity entity = MobileEntity.EMPTY;
                Snowball snowball = board.snowballInPosition(row, col);
                boolean hasMonster = monster.getRow() == row && monster.getCol() == col;

                if(hasMonster){
                    entity = MobileEntity.MONSTER;
                } else if (snowball != null) {
                    entity = MobileEntity.SNOWBALL;
                }

                EntityButton button;

                button = new EntityButton(entity);


                this.add(button, col, row);
                buttons[row][col] = button;
            }
        }
    }

}
