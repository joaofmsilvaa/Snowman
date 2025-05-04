package pt.ipbeja.estig.po2.snowman.app.model;

public class Monster extends MobileElement {

    public Monster(int row, int col) {
        super(row, col);
    }

    @Override
    public boolean move(Direction direction, BoardModel board) {
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case UP:    newRow--; break;
            case DOWN:  newRow++; break;
            case LEFT:  newCol--; break;
            case RIGHT: newCol++; break;
        }

        if (!board.validPosition(newRow, newCol)) {
            return false;
        }

        Snowball snowball = board.snowballInPosition(newRow, newCol);

        if (snowball != null) {
            if (!board.moveSnowball(direction, snowball)) {
                return false;
            }
        }

        row = newRow;
        col = newCol;
        return true;
    }
}
