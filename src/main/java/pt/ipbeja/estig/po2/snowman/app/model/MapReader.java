package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MapReader is responsible for loading game maps from resource files
 * and converting them into a BoardModel instance. Each symbol in the map
 * file corresponds to a specific cell or object type (e.g., snow, block,
 * monster, or small snowball).
 */
public class MapReader {

    String mapName;

    /**
     * Reads a map file from the resources folder and returns a BoardModel.
     * The first line in the file is treated as the map name. Subsequent lines
     * contain symbols separated by whitespace (e.g., "S B M SB").
     *
     * @param resourcePath the path to the map file inside resources (e.g., "/maps/level1.txt")
     * @return a BoardModel constructed from the parsed symbols in the file
     * @throws RuntimeException if the file is not found, empty, or cannot be read
     */
    public BoardModel loadMapFromFile(String resourcePath) {
        // Open the resource as an input stream
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new RuntimeException("Mapa não encontrado: " + resourcePath);
        }

        String mapName = "";
        List<String[]> lines = new ArrayList<>();

        // Read the file line by line
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;

            // Read the map name from the first line
            mapName = reader.readLine();
            if (mapName == null) {
                throw new RuntimeException("Ficheiro de mapa vazio ou inválido: " + resourcePath);
            }

            // Read the map layout starting from the second line
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Split symbols by whitespace and store each row as a String array
                    lines.add(line.trim().split("\\s+"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o mapa: " + e.getMessage());
        }

        // Convert the list of String arrays into a 2D String array and parse
        String[][] map = lines.toArray(new String[0][]);
        return parseMap(map, mapName);
    }

    /**
     * Parses a 2D array of symbol strings into a BoardModel. Each symbol
     * indicates the content type of a cell, such as SNOW, BLOCK, MONSTER, or SMALL_SNOWBALL.
     * After constructing the board content, it initializes the BoardModel, saves its initial state,
     * and sets the map name.
     *
     * @param map     a 2D array of symbol strings representing the map layout
     * @param mapName the name of the map (typically read from the first line of the file)
     * @return a BoardModel populated with terrain, snowballs, and the monster
     */
    public BoardModel parseMap(String[][] map, String mapName) {
        // Prepare collections to hold board content, snowballs, and monster
        List<List<PositionContent>> boardContent = new ArrayList<>();
        List<Snowball> snowballs = new ArrayList<>();
        Monster monster = null;

        // Iterate over each row and column in the symbol array
        for (int row = 0; row < map.length; row++) {
            List<PositionContent> line = new ArrayList<>();
            for (int col = 0; col < map[row].length; col++) {
                String symbol = map[row][col];
                switch (symbol) {
                    case "S" -> line.add(PositionContent.SNOW);
                    case "B" -> line.add(PositionContent.BLOCK);
                    case "M" -> {
                        // Place the monster at this cell; cell itself has no snow
                        line.add(PositionContent.NO_SNOW);
                        monster = new Monster(row, col);
                    }
                    case "SB" -> {
                        // Place a small snowball at this cell; cell has no snow underneath
                        line.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(row, col, SnowballType.SMALL));
                    }
                    // Any unrecognized symbol is treated as no snow
                    default -> line.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(line);
        }

        // Instantiate the BoardModel, save its initial state, and set map name
        BoardModel boardModel = new BoardModel(boardContent, monster, snowballs);
        boardModel.saveState();  // Save the initial snapshot for undo/redo
        setMapName(mapName);    // Store the map name for reference
        return boardModel;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
