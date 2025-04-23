package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> board;
    private Monster monster;
    private List<Snowball> snowballs;

    public BoardModel(List<List<PositionContent>> board, Monster monster, List<Snowball> snowballs) {
        this.board = board;
        this.monster = monster;
        this.snowballs = snowballs;
    }

    public PositionContent getPositionContent(int row, int col) {
        return board.get(row).get(col);
    }

    public boolean moveMonster(Direction direction) {
        return monster.move(direction, this);
    }

    // Outros métodos necessários para a lógica do jogo
}