package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * MobileElement is an abstract class representing any movable object on the board,
 * such as Monster or Snowball. It stores the current position (row, col)
 * and the previous position (prevRow, prevCol) for purposes like animation or undo.
 */
public abstract class MobileElement {
    protected int row;
    protected int col;
    protected int prevRow;
    protected int prevCol;

    ///Constructs a MobileElement at the specified row and column.
    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Attempts to move this element in the given direction on the specified board.
     * Subclasses must implement their own movement logic, updating row/col
     * and setting prevRow/prevCol before moving.
     *
     * @param direction the direction to move (UP, DOWN, LEFT, RIGHT)
     * @param board     reference to the BoardModel for validating and updating state
     * @return true if the move succeeded; false otherwise
     */
    public abstract boolean move(Direction direction, BoardModel board);


    /// Metodos to get an row and an column of the element.
    public int getRow() {return row;
    }

    public int getCol() {return col;
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
