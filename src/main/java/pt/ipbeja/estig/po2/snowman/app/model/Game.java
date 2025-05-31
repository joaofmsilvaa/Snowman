package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;

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

    /**
     * Writes game information to a file when a snowman is completed:
     * - Level name ("map name")
     * - Move list (placeholder example, should use actual moves)
     * - Total move count
     * - Final snowman position
     *
     * @param snowmanPosition final position of the snowman (row and column)
     */
    public void storeGameDetails(Position snowmanPosition, ScoreListener scoreListener) {
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();

        snowmanFile.writeFile(mapName, getMoveHistoryArray(), getMoveCount(), getPlayerName(), snowmanPosition);

        // Criar e notificar pontuação
        Score score = new Score(getPlayerName(), mapName, getMoveCount());
        if (scoreListener != null) {
            scoreListener.onScore(score);
        }
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

