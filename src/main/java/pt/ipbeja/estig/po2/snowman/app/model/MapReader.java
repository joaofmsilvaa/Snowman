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

        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split("\\s+"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o mapa: " + e.getMessage());
        }

        String[][] map = lines.toArray(new String[0][]);
        return parseMap(map);
    }

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
