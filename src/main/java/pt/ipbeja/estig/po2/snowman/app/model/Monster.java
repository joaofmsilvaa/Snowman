package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * The Monster represents the player-controlled character. It extends MobileElement
 * and implements all the movement logic, including pushing snowballs and detecting if the
 * snowmen was completed.
 */
public class Monster extends MobileElement {

    public Monster(int row, int col) {
        super(row, col);
    }


    /**
     * Attempts to move the monster in the specified direction. The method:
     * 1. Calculates the target cell.
     * 2. Checks if that cell is valid (not a BLOCK).
     * 3. If a snowball is in the target cell:
     *    a. If it is part of a stack, try to unstack; if unstacked, movement fails.
     *    b. Otherwise, attempt to push the snowball. If push fails, movement fails.
     * 4. Records the previous position, updates row/col to the new position.
     * 5. Checks if a complete snowman is formed beyond the new position.
     *
     * @param direction the direction to move (UP, DOWN, LEFT, RIGHT)
     * @param board     the game model to query and update
     * @return true if the monster moved (and possibly pushed a snowball); false otherwise
     */
    @Override
    public boolean move(Direction direction, BoardModel board) {
        /// Calculate the adjacent cell in the desired direction
        Position position = new Position(row, col);
        position = position.changePosition(direction);
        int newRow = position.getRow();
        int newCol = position.getCol();

        if (!board.validPosition(newRow, newCol)) {
            return false;
        }

        /// Check if there is a snowball in the target cell
        Snowball snowball = board.getSnowballInPosition(newRow, newCol);
        if (snowball != null) {
            if(board.isSnowballStack(snowball)) {
                board.unstackSnowballs(snowball, direction);

                return false;
            }
            /// if not, will attempt to push the snowball. If push fails, cancel move
            else if (!board.moveSnowball(direction, snowball)){
                return false;
            }
        }

        /// Save the previous coordinates before updating
        setPrevRow(row);
        setPrevCol(col);
        row = newRow;
        col = newCol;

        /// After moving, check for a complete snowman one cell further in same direction
        Position checkposition = new Position(row, col).changePosition(direction);
        board.checkCompleteSnowman(checkposition);

        return true;
    }


}