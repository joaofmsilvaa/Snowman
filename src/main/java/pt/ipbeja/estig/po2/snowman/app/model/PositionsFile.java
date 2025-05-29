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

    public void storePosition(Position previous, Position current) {
        try (FileWriter myWriter = new FileWriter(positionsFileName, true)) {
            myWriter.write(current.formatDetails(previous.getRow(), previous.getCol(), current.getRow(), current.getCol()));
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
