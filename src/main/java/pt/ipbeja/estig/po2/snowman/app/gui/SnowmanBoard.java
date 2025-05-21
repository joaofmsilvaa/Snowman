package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBoard extends GridPane implements View {

    private BoardModel boardModel;
    private BoardButton[][] buttons = new BoardButton[boardModel.SIZE][boardModel.SIZE];

    // Construtor que inicializa o SnowmanBoard com o modelo do tabuleiro
    public SnowmanBoard(BoardModel boardModel) {
        this.boardModel = boardModel;
        drawBoard();
    }

    private void drawBoard() {
        monster = new Monster(2,0);

        for (int i = 0; i < boardModel.SIZE; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < boardModel.SIZE; j++) {
                if(i == 0) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        Snowball snowball = new Snowball(1,0, SnowballType.SMALL);
        snowballs.add(snowball);
        boardModel = new BoardModel(content, monster, snowballs);
    }

}
