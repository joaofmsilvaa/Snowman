package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * Position represents a coordinate (row, col) on the game board.
 * It provides methods to shift the position in a given direction,
 * convert a column index to a letter, and format move details.
 */
public class Position {
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns a new Position one step away in the given direction.
     * Does not modify the original Position.
     *
     * @param direction the direction to move (UP, DOWN, LEFT, RIGHT)
     * @return a new Position shifted by one cell in that direction
     */
    public Position changePosition(Direction direction){
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case UP:    newRow--; break;
            case DOWN:  newRow++; break;
            case LEFT:  newCol--; break;
            case RIGHT: newCol++; break;
        }

        return new Position(newRow, newCol);
    }

    /**
     * Converts a zero-based column index into an uppercase letter.
     * For example, 0 -> "A", 1 -> "B", etc.
     *
     * @param value the column index (0-based)
     * @return the corresponding column letter
     */
    public String convertToLetter(int value){

        return String.valueOf((char) ('A' + value));
    }

    /**
     * Formats a move from a previous position to a current position as a string.
     * Example output: "(2,B) -> (3,C)\n"
     *
     * @param previousRow    row index of the previous position
     * @param previousCol    column index of the previous position
     * @param currentRow     row index of the current position
     * @param currentCol     column index of the current position
     * @return a formatted string describing the move
     */
    public String formatDetails(int previousRow, int previousCol, int currentRow, int currentCol){
        String colLetter = convertToLetter(currentCol);
        String previousColLetter = convertToLetter(previousCol);

        String previous = "(" + previousRow + "," + previousColLetter + ")";
        String current = "(" + currentRow + "," + colLetter + ")";

        return previous + " -> " + current + "\n";
    }

    public int getRow() {

        return row;
    }

    public int getCol() {

        return col;
    }
}
