package pt.ipbeja.estig.po2.snowman.app.model;

public class Position {
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

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

    public String convertToLetter(int value){
        return String.valueOf((char) ('A' + value));
    }

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
