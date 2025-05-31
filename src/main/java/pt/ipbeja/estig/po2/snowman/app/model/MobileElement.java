package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * MobileElement é uma classe abstrata que representa qualquer objeto móvel
 * no tabuleiro Monster e Snowball. Guarda a posição atual
 * (row, col) e a posição anterior (prevRow, prevCol) para efeitos de animação
 * ou “undo”.
 */

public abstract class MobileElement {
    protected int row;
    protected int col;
    protected int prevRow;
    protected int prevCol;

    //construtor inicializa a posicao do elemento
    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Metodo abstrato para mover o elemento
    public abstract boolean move(Direction direction, BoardModel board);

    // Metodos para obter a linha e a coluna do elemento
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPrevRow() {
        return prevRow;
    }
    public int getPrevCol() {
        return prevCol;
    }

    public void setPrevRow(int prevRow) {
        this.prevRow = prevRow;
    }

    public void setPrevCol(int prevCol) {
        this.prevCol = prevCol;
    }
}
