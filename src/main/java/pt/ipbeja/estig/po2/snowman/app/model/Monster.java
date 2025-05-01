package pt.ipbeja.estig.po2.snowman.app.model;

public class Monster extends MobileElement {

    public Monster(int row, int col) {
        super(row, col);
    }

    //para usar quando ele for mover
    @Override
    public void move(int newRow, int newCol) {

    }

    @Override
    public boolean move(Direction direction, BoardModel board) {

        switch (direction) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> col--;
            case RIGHT -> col++;
        }

        return false;

    }
}
