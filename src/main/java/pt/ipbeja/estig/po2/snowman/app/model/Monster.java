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
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case LEFT:
                newCol--;
                break;
            case RIGHT:
                newCol++;
                break;
        }

        if (validPosition(newRow, newCol, board)) {
            row = newRow;
            col = newCol;
            return true;
        }

        return false;

    }
}
