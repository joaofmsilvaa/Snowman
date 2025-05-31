package pt.ipbeja.estig.po2.snowman.app.model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DetailsFile é abstrata e serve de base para gravar em ficheiro
 * as informações do nível quando o boneco é completado.
 * Contém métodos para criar o ficheiro e escrever o mapa, movimentos,
 * contagem total e posição final do boneco.
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

    /// Cria um ficheiro vazio com o nome definido em fileName.
    public void createFile() {
        try {
            File myObj = new File(fileName);
            fileCreated = myObj.createNewFile();
        } catch (IOException e) {
            fileCreated = false;
        }
    }

    /*
        Escreve um ficheiro com o Mapa utilizado, os movimentos, o total de jogadas e a
        posição final do boneco de neve
     */
    public void writeFile(String map, String[] moves, int moveCount, Position snowmanPosition) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Mapa: " + map);
            writer.write("\n" +"Movimentos:\n");

            for (String move : moves) {
                writer.write(move);
            }
            writer.write("\n" + "Total de jogadas: " + moveCount);

            /// Converte a coluna numa letra e escreve a posição final
            String colLetter = snowmanPosition.convertToLetter(snowmanPosition.getCol());
            writer.write("\n" + "Posição final do boneco de neve: (" + snowmanPosition.getRow() + "," + colLetter + ")\n");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
    }


}
