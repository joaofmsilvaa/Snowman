package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapReader {

    public BoardModel loadMapFromFile(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new RuntimeException("Mapa não encontrado: " + resourcePath);
        }

        String mapName = "";
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;

            // Lê o nome do mapa da primeira linha
            mapName = reader.readLine();
            if (mapName == null) {
                throw new RuntimeException("Ficheiro de mapa vazio ou inválido: " + resourcePath);
            }

            // Lê as linhas do mapa a partir da segunda linha
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim().split("\\s+"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o mapa: " + e.getMessage());
        }

        String[][] map = lines.toArray(new String[0][]);
        return parseMap(map, mapName);
    }

    public BoardModel parseMap(String[][] map, String mapName) {
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

        BoardModel boardModel = new BoardModel(boardContent, monster, snowballs);
        boardModel.setMapName(mapName); // ← guardar o nome do mapa
        return boardModel;
    }

}
