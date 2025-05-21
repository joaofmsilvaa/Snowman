package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    public static final int SIZE = 5;

    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
    }

    public BoardModel(List<List<PositionContent>> content,Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = snowballs;
        this.boardContent = content;
    }

    public void startGame(){
        monster = new Monster(2,0);

        for (int i = 0; i < SIZE; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                if(i == 0) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(row);
        }

        Snowball snowball = new Snowball(1,0, SnowballType.SMALL);
        snowballs.add(snowball);
    }


    public PositionContent getPositionContent(int row, int col) {
        return boardContent.get(row).get(col);
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

    public Monster getMonster() {
        return monster;
    }

    public List<Snowball> getSnowballs() {
        return snowballs;
    }
}