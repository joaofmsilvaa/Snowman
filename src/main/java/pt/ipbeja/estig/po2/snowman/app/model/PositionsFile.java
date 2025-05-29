package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PositionsFile {
    private final String positionsFileName;
    private boolean fileCreated;

    public PositionsFile(String fileName){
        this.positionsFileName = fileName;
        try {
            File myObj = new File(positionsFileName);
            fileCreated = myObj.createNewFile();
        } catch (IOException e) {
            fileCreated = false;
        }
    }

    public String convertToLetter(int value){
        return String.valueOf((char) ('A' + value));
    }

    public String formatDetails(int previousRow, int previousCol, int row, int col){
        String colLetter = convertToLetter(col);
        String previousColLetter = convertToLetter(previousCol);

        String previous = "(" + previousRow + "," + previousColLetter + ")";
        String current = "(" + row + "," + colLetter + ")";

        return previous + " -> " + current + "\n";
    }

    public void storePosition(int previousRow, int previousCol, int row, int col) {
        try (FileWriter myWriter = new FileWriter(positionsFileName, true)) {
            myWriter.write(formatDetails(previousRow, previousCol, row, col));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFileCreated() {
        return fileCreated;
    }

    public String getPositionsFileName() {
        return positionsFileName;
    }
}
