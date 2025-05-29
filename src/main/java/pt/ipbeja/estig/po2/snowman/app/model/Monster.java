package pt.ipbeja.estig.po2.snowman.app.model;

public class Monster extends MobileElement {

    public Monster(int row, int col) {
        super(row, col);
    }

    @Override
    public boolean move(Direction direction, BoardModel board) {
        Position position = new Position(row, col);
        position = position.changePosition(direction);
        int newRow = position.getRow();
        int newCol = position.getCol();

        if (!board.validPosition(newRow, newCol)) {
            return false;
        }

        Snowball snowball = board.snowballInPosition(newRow, newCol);

        if (snowball != null) {
            if(board.isSnowballStack(snowball)) {
                board.unstackSnowballs(snowball, direction);

                return false;
            }
            else if (!board.moveSnowball(direction, snowball)){
                return false;
            }
        }

        setPrevRow(row);
        setPrevCol(col);
        row = newRow;
        col = newCol;

        // Verificar o snowman nas posições adjacentes relevantes
        checkPotentialSnowman(board, direction);
        return true;
    }


    private void checkPotentialSnowman(BoardModel board, Direction direction) {
        int snowmanRow = row;
        int snowmanCol = col;

        // Verifica na posição oposta ao movimento
        switch (direction) {
            case UP:
                snowmanRow++;
                break;
            case DOWN:
                snowmanRow--;
                break;
            case LEFT:
                snowmanCol++;
                break;
            case RIGHT:
                snowmanCol--;
                break;
        }

        Position position = new Position(snowmanRow, snowmanCol);
        board.checkCompleteSnowman(position);
    }


}