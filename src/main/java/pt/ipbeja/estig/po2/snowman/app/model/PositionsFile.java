package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * PositionsFile extends DetailsFile to append each move's positions
 * into the same file previously created. It writes only the move details,
 * preserving earlier content.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class PositionsFile extends DetailsFile {

    /// constructor calls the superclass constructor.
    public PositionsFile() {
        super();
    }

    /**
     * Appends the move from a previous position to the current position
     * into the existing file. Uses Position.formatDetails(...) to format
     * the move as "(rowPrev,colPrev) -> (rowCurr,colCurr)\n".
     *
     * @param previous the Position before the move
     * @param current  the Position after the move
     */
    public void storePosition(Position previous, Position current) {
        try (FileWriter myWriter = new FileWriter(getFileName(), true)) {
            myWriter.write(current.formatDetails(previous.getRow(), previous.getCol(), current.getRow(), current.getCol()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
