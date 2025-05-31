package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * Snowball represents a snowball on the board. It extends MobileElement
 * and manages its type (size or stack). It can move, grow over snow,
 * and stack with other snowballs.
 */
public class Snowball extends MobileElement {
    private SnowballType type;

    /// Constructs a Snowball at the given row and column with a specific type.
    public Snowball(int row, int col, SnowballType type) {
        super(row, col);
        this.type = type;
    }


    public SnowballType getType() {
        return type;
    }

    public void setType(SnowballType type) {
        this.type = type;
    }

    /// Increases the snowball's type  SMALL → MID → BIG.
    public void increaseSnowballType() {
        switch (type) {
            case SMALL -> setType(SnowballType.MID);
            case MID -> setType(SnowballType.BIG);
        }
    }


    /**
     * Checks if this snowball can stack on another snowball.
     * SMALL can stack on MID or BIG (or BIG_MID).
     * MID can stack on BIG. BIG cannot stack on anything.
     *
     * @param other the snowball to stack on
     * @return true if stacking is allowed; false otherwise
     */
    public boolean canStackOn(Snowball other) {
        switch (this.type) {
            case SMALL:
                return other.type == SnowballType.MID ||
                        other.type == SnowballType.BIG || other.type == SnowballType.BIG_MID;
            case MID:
                return other.type == SnowballType.BIG;

            case BIG: // BIG não pode ser empilhada
                return false;
        }
        return false;
    }

    /**
     * Determines the resulting stack type when this snowball is stacked
     * on the given other snowball. Returns null if stacking is not allowed.
     * Possible results:
     * - SMALL on MID → MID_SMALL
     * - SMALL on BIG → BIG_SMALL
     * - MID on BIG → BIG_MID
     * - SMALL on BIG_MID → COMPLETE (full snowman)
     *
     * @param other the snowball on which to stack
     * @return the new stack type, or null if not allowed
     */
    public SnowballType stackOn(Snowball other) {
        if (!canStackOn(other)) {
            return null; // Não pode empilhar
        }

        if (this.type == SnowballType.SMALL && other.type == SnowballType.MID) {
            return SnowballType.MID_SMALL;
        }
        else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG) {
            return SnowballType.BIG_SMALL;
        }
        else if (this.type == SnowballType.MID && other.type == SnowballType.BIG) {
            return SnowballType.BIG_MID;
        }
        else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG_MID) {
            return SnowballType.COMPLETE;
        }

        return null;
    }


    /**
     * Attempts to move the snowball one cell in the given direction.
     * If a complete snowman (COMPLETE) type, it cannot move.
     * Steps:
     * 1. Reject move if type is COMPLETE.
     * 2. Compute target cell coordinates.
     * 3. If invalid (blocked or out of bounds), return false.
     * 4. If another snowball is in target:
     *    - Attempt to stack (board.tryStackSnowballs).
     *    - Return result of stacking attempt.
     * 5. If target cell has snow, call increaseSnowballType().
     * 6. Update row and col to target, return true.
     *
     * @param direction direction to move (UP, DOWN, LEFT, RIGHT)
     * @param board     reference to the BoardModel for validation and updates
     * @return true if the move or stack was successful; false otherwise
     */

    @Override
    public boolean move(Direction direction, BoardModel board) {
        // Snowman completo não pode mover
        if (this.type == SnowballType.COMPLETE) {
            return false;
        }

        Position position = new Position(row, col);
        position = position.changePosition(direction);
        int newRow = position.getRow();
        int newCol = position.getCol();

        if (!board.validPosition(newRow, newCol)) {
            return false;
        }
        // Verifica se há outra bola no destino
        Snowball target = board.snowballInPosition(newRow, newCol);
        if (target != null) {

            return board.tryStackSnowballs(this, target);
        }

        PositionContent destination = board.getPositionContent(newRow, newCol);

        if (destination == PositionContent.BLOCK) {
            return false;
        }

        if (destination == PositionContent.SNOW) {
            increaseSnowballType();
        }

        row = newRow;
        col = newCol;
        return true;
    }

    /**
     * Checks if this snowball is a partial stack (MID_SMALL, BIG_MID, or BIG_SMALL).
     *
     * @return true if the type represents a partial stack, if not false
     */
    public boolean isSnowballStack(){
        SnowballType type = this.getType();
        if(type == SnowballType.BIG_MID || type == SnowballType.MID_SMALL ||
                type == SnowballType.BIG_SMALL) {
            return true;
        }

        return false;
    }

}