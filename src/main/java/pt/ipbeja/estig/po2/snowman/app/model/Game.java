package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String playerName;
    private final List<String> moveHistory = new ArrayList<>();
    private String mapName;

    public Game(String playerName, String mapName) {
        this.playerName = playerName;
        this.mapName = mapName;
    }

    public void onMove(Position from, Position to) {
        moveHistory.add(to.formatDetails(from.getRow() + 1, from.getCol(), to.getRow() + 1, to.getCol()));
    }

    /// Retorna o número de movimentos realizados
    public int getMoveCount() {
        return moveHistory.size();
    }

    /// Retorna o nome do jogador atual
    public String getPlayerName() {
        return playerName;
    }

    public String[] getMoveHistoryArray() {
        return moveHistory.toArray(new String[0]);
    }

    public String getMapName(){
        return mapName;
    }

    /// Define o nome do jogado
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

}

