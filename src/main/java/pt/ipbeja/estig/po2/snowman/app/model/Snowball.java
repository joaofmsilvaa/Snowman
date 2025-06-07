package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * Represents a snowball on the game board, managing its type and movement logic.
 * Extends MobileElement to track row and column positions. A Snowball can grow over snow,
 * stack with other snowballs to form larger types, and ultimately form a complete snowman.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class Snowball extends MobileElement {
    private SnowballType type;

    /**
     * Constructs a Snowball at the specified row and column with the given type.
     *
     * @param row  the initial row position of this snowball
     * @param col  the initial column position of this snowball
     * @param type the SnowballType (SMALL, MID, BIG, or a stacked variant)
     */
    public Snowball(int row, int col, SnowballType type) {
        super(row, col);
        this.type = type;
    }

    /**
     * Returns the current type of this snowball.
     *
     * @return the SnowballType of this instance
     */
    public SnowballType getType() {
        return type;
    }

    /**
     * Sets the type of this snowball.
     *
     * @param type the new SnowballType to assign
     */
    public void setType(SnowballType type) {
        this.type = type;
    }

    /**
     * Increases the size of this snowball over snow: SMALL → MID → BIG.
     * Does nothing if the type is already BIG or a stacked variant.
     */
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
        // Determine stacking eligibility
        switch (this.type) {
            case SMALL:
                return other.type == SnowballType.MID ||
                        other.type == SnowballType.BIG || other.type == SnowballType.BIG_MID;
            case MID:
                return other.type == SnowballType.BIG;

            case BIG: // BIG can´t stack on
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
            return null; // Can't Stack on
        }

        // Compute resulting type based on combination
        if (this.type == SnowballType.SMALL && other.type == SnowballType.MID) {
            return SnowballType.MID_SMALL;
        } else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG) {
            return SnowballType.BIG_SMALL;
        } else if (this.type == SnowballType.MID && other.type == SnowballType.BIG) {
            return SnowballType.BIG_MID;
        } else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG_MID) {
            return SnowballType.COMPLETE;
        }

        return null;
    }


    /**
     * Attempts to move this snowball one cell in the specified direction.
     * Movement rules:
     * 1. A COMPLETE snowman cannot move.
     * 2. Compute target cell coordinates based on direction.
     * 3. If the target is invalid (out of bounds or blocked), return false.
     * 4. If another snowball occupies the target, attempt to stack via board.tryStackSnowballs().
     * 5. If the target cell has snow, remove the snow and call increaseSnowballType().
     * 6. Update this snowball's position to the target and return true.
     *
     * @param direction the Direction to move (UP, DOWN, LEFT, RIGHT)
     * @param board     reference to the BoardModel for validation and updates
     * @return true if the move or stack operation succeeds; false otherwise
     */
    @Override
    public boolean move(Direction direction, BoardModel board) {
        // Prevent movement if already a complete snowman
        if (this.type == SnowballType.COMPLETE) {
            return false;
        }

        // Calculate new target position
        Position position = new Position(row, col);
        position = position.changePosition(direction);
        int newRow = position.getRow();
        int newCol = position.getCol();

        // Check if target position is valid on the board
        if (!board.validPosition(newRow, newCol)) {
            return false;
        }

        // If another snowball is present, attempt to stack
        Snowball target = board.getSnowballInPosition(newRow, newCol);
        if (target != null) {

            return board.tryStackSnowballs(this, target);
        }

        // Inspect terrain of the target cell
        PositionContent destination = board.getPositionContent(newRow, newCol);
        if (destination == PositionContent.BLOCK) {
            return false;
        }

        // If it's snow, consume it and grow
        if (destination == PositionContent.SNOW) {
            board.setPositionContent(newRow, newCol, PositionContent.NO_SNOW);
            increaseSnowballType();
        }

        // Update position fields
        row = newRow;
        col = newCol;
        return true;
    }

    /**
     * Checks if this snowball instance represents a partial stack:
     * types MID_SMALL, BIG_MID, or BIG_SMALL all count as a “stack.”
     *
     * @return true if this snowball is a stacked variant; false otherwise
     */
    public boolean isSnowballStack() {
        // Return true for known partial-stack types
        SnowballType type = this.getType();
        if (type == SnowballType.BIG_MID || type == SnowballType.MID_SMALL ||
                type == SnowballType.BIG_SMALL) {
            return true;
        }

        return false;
    }


    /**
     * Returns the bottom component of a stacked snowball.
     * <p>
     * Given a combined snowball instance, this method determines the appropriate
     * “bottom” snowball based on the stack’s type. For example, a MID_SMALL stack
     * has a MID bottom, while BIG_MID and BIG_SMALL stacks share the same bottom size.
     *
     * @return a new Snowball representing the bottom part, or null if not applicable
     */
    public Snowball getBottom() {
        // Determine bottom based on stack type
        return switch (this.getType()) {

            // a MID_SMALL stack has a MID snowball at its bottom position
            case MID_SMALL -> new Snowball(this.getRow(), this.getCol(), SnowballType.MID);

            // BIG_MID and BIG_SMALL stacks both have a BIG snowball as the bottom
            case BIG_MID, BIG_SMALL -> new Snowball(this.getRow(), this.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    /**
     * Returns the top component of a stacked snowball, positioned in the specified direction.
     * <p>
     * This method calculates the position of the “top” part by moving one cell from the
     * base of the stacked snowball in the given direction. It then determines the appropriate
     * SnowballType for the top based on the stack’s type (e.g., MID_SMALL or BIG_SMALL have a SMALL top,
     * BIG_MID has a MID top). If the stack type does not correspond to a valid top part, it returns null.
     *
     * @param direction the Direction in which the top part should be placed
     * @return a new Snowball representing the top part at its correct position, or null if not applicable
     */
    public Snowball getTop(Direction direction) {
        //Calculate the target position for the top component
        // Start from the base’s row and column, then move one cell in the specified direction
        Position position = new Position(this.getRow(),
                this.getCol()).changePosition(direction);

        // Determine the SnowballType of the top component based on the stack type
        SnowballType type = switch (this.getType()) {
            case MID_SMALL, BIG_SMALL -> SnowballType.SMALL;
            case BIG_MID -> SnowballType.MID;
            default -> null;
        };

        // Create and return the new Snowball if type is valid, otherwise return null
        return type == null
                ? null
                : new Snowball(position.getRow(), position.getCol(), type);
    }
}