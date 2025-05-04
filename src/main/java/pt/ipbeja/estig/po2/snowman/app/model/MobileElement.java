package pt.ipbeja.estig.po2.snowman.app.model;

public abstract class MobileElement {
    protected int row;
    protected int col;

    //construtor inicializa a posicao do elemento
    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Metodos para obter a linha e a coluna do elemento
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean validPosition(int newRow, int newCol, BoardModel board) {
        try{
            return board.getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    // Metodo abstrato para mover o elemento
    public abstract boolean move(Direction direction, BoardModel board);
}

