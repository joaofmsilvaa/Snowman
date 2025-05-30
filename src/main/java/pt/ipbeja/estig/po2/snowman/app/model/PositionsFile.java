package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PositionsFile extends DetailsFile{


    public PositionsFile(){
        super();
    }

    public void storePosition(Position previous, Position current) {
        try (FileWriter myWriter = new FileWriter(getFileName(), true)) {
            myWriter.write(current.formatDetails(previous.getRow(), previous.getCol(), current.getRow(), current.getCol()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
