package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.View;

public class SnowmanBoard extends GridPane implements View {

    private BoardModel boardModel;

    // Construtor que inicializa o SnowmanBoard com o modelo do tabuleiro
    public SnowmanBoard(BoardModel boardModel) {
        this.boardModel = boardModel;
        drawBoard();
    }

    private void drawBoard() {
    }

}
