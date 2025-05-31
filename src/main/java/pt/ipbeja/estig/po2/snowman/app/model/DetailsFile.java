package pt.ipbeja.estig.po2.snowman.app.model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DetailsFile is an abstract base class for writing level information to a file
 * when a snowman is completed. It provides methods to create the file and write
 * the map name, move list, total moves, and final snowman position.
 */
public abstract class DetailsFile {

    private String fileName;
    private boolean fileCreated;

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


    public void writeFile(String map, String[] mapPanel, String[] moves, int moveCount, String playerName, Position snowmanPosition) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Mapa: " + map);
            writer.write("\n" +"Movimentos:\n");
            for (String move : moves) {
                writer.write(move);
            }

            writer.write("\nJogador: " + playerName);
            writer.write("\n" + "Total de jogadas: " + moveCount);

            /// Convert the column index to a letter and write the final position
            String colLetter = snowmanPosition.convertToLetter(snowmanPosition.getCol());
            writer.write("\n" + "Posição final do boneco de neve: (" + snowmanPosition.getRow() + "," + colLetter + ")\n");

            writer.write("\n\nMapa final:\n");
            for (String line : mapPanel) {
                writer.write(line + "\n");
            }

        } catch (IOException e) {
            System.err.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
    }


}
