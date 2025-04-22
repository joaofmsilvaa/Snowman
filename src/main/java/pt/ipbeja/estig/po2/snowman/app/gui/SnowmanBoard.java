package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.css.Size;
import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.app.model.Mark;
import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;
import pt.ipbeja.estig.po2.snowman.app.model.View;

import java.util.ArrayList;
import java.util.List;

public class SnowmanBoard extends GridPane implements View {

    private BoardModel boardModel;
    private static final int CELL_SIZE = 50; // Tamanho de cada célula

    public SnowmanBoard(BoardModel boardModel) {
        this.boardModel = boardModel;
        drawBoard();
    }

    private void drawBoard() {
    }

}
