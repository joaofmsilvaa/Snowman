package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class MapReader {

    /**
     * Lê um mapa textual e retorna um BoardModel preenchido.
     * @param map Array de strings representando o mapa
     * @return BoardModel inicializado com base no mapa
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
