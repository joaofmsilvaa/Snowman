package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> board;
    private Monster monster;
    private List<Snowball> snowballs;
    public static final int SIZE = 5;

    public BoardModel(List<List<PositionContent>> board, Monster monster, List<Snowball> snowballs) {
        this.board = board;
        this.monster = monster;
        this.snowballs = snowballs;
    }

    public PositionContent getPositionContent(int row, int col) {
        return board.get(row).get(col);
    }

    public boolean validPosition(int newRow, int newCol) {
        try{
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    public Snowball snowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if(snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }

        return null;
    }

    public boolean moveMonster(Direction direction) {
        return monster.move(direction, this);
    }

    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }
}