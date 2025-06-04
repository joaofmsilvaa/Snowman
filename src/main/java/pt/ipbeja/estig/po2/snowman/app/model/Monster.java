package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Monster represents the player-controlled character. It extends MobileElement
 * and implements all the movement logic, including pushing snowballs and detecting if the
 * snowmen was completed.
 */
import java.util.Stack;

public class Monster extends MobileElement {
    private final Stack<Position> undoStack = new Stack<>();
    private final Stack<Position> redoStack = new Stack<>();
    private int moveCount = 0;

    public Monster(int row, int col) {
        super(row, col);
        undoStack.push(new Position(row, col));
    }

    @Override
    public boolean move(Direction direction, BoardModel board) {
        Position target = new Position(row, col).changePosition(direction);
        int newRow = target.getRow(), newCol = target.getCol();

        if (!board.validPosition(newRow, newCol)) return false;

        Snowball snowball = board.getSnowballInPosition(newRow, newCol);
        if (snowball != null) {
            if (board.isSnowballStack(snowball)) {
                board.unstackSnowballs(snowball, direction);
                return false;
            } else if (!board.moveSnowball(direction, snowball)) {
                return false;
            }
        }

        setPrevRow(row);
        setPrevCol(col);
        row = newRow;
        col = newCol;

        board.checkCompleteSnowman(target.changePosition(direction));

        undoStack.push(new Position(row, col));
        redoStack.clear(); // reset redo history after any new move
        moveCount++;
        return true;
    }

    public Position undo() {
        if (undoStack.size() > 1) {
            Position last = undoStack.pop();
            redoStack.push(last);

            Position prev = undoStack.peek();
            setPrevRow(row); // <-- importante!
            setPrevCol(col);
            this.row = prev.getRow();
            this.col = prev.getCol();
            moveCount--;

            return prev;
        }

        return getPosition();
    }

    public Position redo() {
        if (!redoStack.isEmpty()) {
            Position redoPos = redoStack.pop();
            setPrevRow(row); // <-- importante!
            setPrevCol(col);
            undoStack.push(redoPos);

            this.row = redoPos.getRow();
            this.col = redoPos.getCol();
            moveCount++;
            return redoPos;
        }
        return getPosition();
    }

    public Position getPosition() {
        return new Position(row, col);
    }

    public int getMoveCount(){
        return moveCount;
    }
}
