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
        // Verifica na posição oposta ao movimento
        switch (direction) {
            case UP:
                board.checkCompleteSnowman(row+1, col); // Verificar abaixo
                break;
            case DOWN:
                board.checkCompleteSnowman(row-1, col); // Verificar acima
                break;
            case LEFT:
                board.checkCompleteSnowman(row, col+1); // Verificar à direita
                break;
            case RIGHT:
                board.checkCompleteSnowman(row, col-1); // Verificar à esquerda
                break;
        }
    }


}