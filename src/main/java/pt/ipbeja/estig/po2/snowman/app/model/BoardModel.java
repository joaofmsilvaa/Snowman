package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    //aqui fazemos um modelo e no snowmanboard implementamos as cenas da board
    private List<List<PositionContent>> board;

    public BoardModel(int rows, int cols) {
        board = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                row.add(PositionContent.NO_SNOW); // Inicializa todas as posições como sem neve
            }
            board.add(row);
        }
    }
}
