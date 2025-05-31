package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MapReader reads a text file that describes the game map and creates
 * a BoardModel object. Each symbol in the file stands for a cell or
 * object type (snow, block, monster, small snowball).
 */
public class MapReader {

    /// Le um ficheiro de mapa e devolve um BoardModel /**
    ///      * Reads a map file from the resources folder and returns a BoardModel.
    ///      * The file must have lines of symbols separated by spaces, such as "S B M SB".
    ///      *
    ///      * @param resourcePath the path to the map file inside resources (e.g., "/maps/level1.txt")
    ///      * @return a BoardModel built from the symbols in the file
    ///      * @throws RuntimeException if the file is not found or cannot be read
    ///      */
    public BoardModel loadMapFromFile(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new RuntimeException("Mapa não encontrado: " + resourcePath);
        }

        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            /// Read each line and split it by whitespace into symbols
            while ((line = reader.readLine()) != null) {
                lines.add(line.split("\\s+"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o mapa: " + e.getMessage());
        }

        String[][] map = lines.toArray(new String[0][]);
        return parseMap(map);
    }

    /**
     * Turns a 2D array of symbols into a BoardModel. Each symbol is read as:
     *
     *   "S"  = a cell with snow (PositionContent.SNOW)
     *   "B"  = an impassable block (PositionContent.BLOCK)
     *   "M"  = the monster's start position (PositionContent.NO_SNOW + new Monster)
     *   "SB" = a small snowball (PositionContent.NO_SNOW + new Snowball of type SMALL
     *   any other symbol = a normal cell with no snow (PositionContent.NO_SNOW)
     *
     *
     * @param map a 2D array of strings where each string is a symbol
     * @return a BoardModel that contains the board layout, the monster, and the snowballs
     */
    public BoardModel parseMap(String[][] map) {
        List<List<PositionContent>> boardContent = new ArrayList<>();
        List<Snowball> snowballs = new ArrayList<>();
        Monster monster = null;

        for (int row = 0; row < map.length; row++) {
            List<PositionContent> line = new ArrayList<>();
            for (int col = 0; col < map[row].length; col++) {
                String symbol = map[row][col];
                switch (symbol) {
                    case "S" -> line.add(PositionContent.SNOW);
                    case "B" -> line.add(PositionContent.BLOCK);
                    case "M" -> {
                        line.add(PositionContent.NO_SNOW);
                        monster = new Monster(row, col);
                    }
                    case "SB" -> {
                        line.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(row, col, SnowballType.SMALL));
                    }
                    default -> line.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(line);
        }

        return new BoardModel(boardContent, monster, snowballs);
    }
}
