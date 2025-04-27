package pt.ipbeja.estig.po2.snowman.app.model;

public class Monster extends MobileElement {
    MobileElement position;

    public Monster(MobileElement position) {
        super(position.row, position.col);
        this.position = position;
    }


    //para usar quando ele for mover
    @Override
    public void move(int newRow, int newCol) {

    }

    @Override
    public boolean move(Direction direction, BoardModel board) {
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }
        return false; // so para nao dar erro
    }
}
