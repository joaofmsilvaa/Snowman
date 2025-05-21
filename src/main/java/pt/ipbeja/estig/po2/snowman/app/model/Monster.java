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

        // Verifica snowman nas posições adjacentes relevantes
        checkPotentialSnowman(board, direction);
        return true;
    }

    private void checkPotentialSnowman(BoardModel board, Direction direction) {
        // Verifica na posição oposta ao movimento
        switch (direction) {
            case UP:
                board.checkCompleteSnowman(row+1, col); // Verifica abaixo
                break;
            case DOWN:
                board.checkCompleteSnowman(row-1, col); // Verifica acima
                break;
            case LEFT:
                board.checkCompleteSnowman(row, col+1); // Verifica à direita
                break;
            case RIGHT:
                board.checkCompleteSnowman(row, col-1); // Verifica à esquerda
                break;
        }
    }
}