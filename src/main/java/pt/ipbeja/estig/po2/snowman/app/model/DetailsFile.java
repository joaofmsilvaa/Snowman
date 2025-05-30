package pt.ipbeja.estig.po2.snowman.app.model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class DetailsFile {

    private String fileName;
    private boolean fileCreated;

    public DetailsFile() {
    }

    public void createFile() {
        try {
            File myObj = new File(fileName);
            fileCreated = myObj.createNewFile();
        } catch (IOException e) {
            fileCreated = false;
        }
    }

    public void writeFile(String map, String[] moves, int moveCount, Position snowmanPosition) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Mapa: " + map);
            writer.write("\n" +"Movimentos:\n");

            for (String move : moves) {
                writer.write(move);
            }
            writer.write("\n" + "Total de jogadas: " + moveCount);

            String colLetter = snowmanPosition.convertToLetter(snowmanPosition.getCol());
            writer.write("\n" + "Posição final do boneco de neve: (" + snowmanPosition.getRow() + "," + colLetter + ")\n");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
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
}
