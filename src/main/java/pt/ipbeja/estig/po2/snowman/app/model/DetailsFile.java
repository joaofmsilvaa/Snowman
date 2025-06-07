package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract base class for creating and writing details to a file when a snowman is completed.
 * <p>
 * This class provides functionality to:
 * 1. Set a filename for the details file.
 * 2. Create the file on disk.
 * 3. Write level information (map name, move list, player, move count, final position, and final map)
 * to the file.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public abstract class DetailsFile {

    private String fileName;
    private boolean fileCreated;

    //Default constructor
    public DetailsFile() {
    }

    public String getFileName() {

        return fileName;
    }

    public boolean isFileCreated() {

        return fileCreated;
    }

    public void setFilename(String filename) {

        this.fileName = filename;
    }

    /**
     * Attempts to create an empty file with the name specified in fileName.
     * If the file already exists or an error occurs, fileCreated is set to false.
     */
    public void createFile() {
        try {
            File myObj = new File(fileName);
            fileCreated = myObj.createNewFile();
        } catch (IOException e) {
            fileCreated = false;
        }
    }

    /**
     * Writes the game details to the previously created file.
     * <p>
     * The following data is written to the file in this order:
     * 1. Map name
     * 2. List of moves
     * 3. Player name
     * 4. Total number of moves
     * 5. Final snowman position (with column converted to a letter)
     * 6. Final map layout (one row per line)
     *
     * @param map             the name of the current map (or "Unknown" if null)
     * @param mapPanel        an array of strings representing the final map layout
     * @param moves           an array of move strings (e.g., "UP", "LEFT", etc.)
     * @param moveCount       the total number of moves made in this game
     * @param playerName      the name of the player (or "Unknown" if null)
     * @param snowmanPosition the Position where the snowman was completed
     */
    public void writeFile(String map, String[] mapPanel, String[] moves, int moveCount, String playerName, Position snowmanPosition) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write map name and move list
            writer.write("Mapa: " + map);
            writer.write("\n" + "Movimentos:\n");
            for (String move : moves) {
                writer.write(move);
            }

            // Write player name and total moves
            writer.write("\nJogador: " + playerName);
            writer.write("\n" + "Total de jogadas: " + moveCount);

            // Convert column index to letter and write final snowman position
            String colLetter = snowmanPosition.convertToLetter(snowmanPosition.getCol());
            writer.write("\n" + "Posição final do boneco de neve: (" + snowmanPosition.getRow() + "," + colLetter + ")\n");

            //Write the final map layout
            writer.write("\n\nMapa final:\n");
            for (String line : mapPanel) {
                writer.write(line + "\n");
            }

        } catch (IOException e) {
            System.err.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
    }


}
